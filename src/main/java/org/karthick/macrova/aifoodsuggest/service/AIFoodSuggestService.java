package org.karthick.macrova.aifoodsuggest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.karthick.macrova.aichatservice.AIPrompts;
import org.karthick.macrova.aichatservice.LLMService;
import org.karthick.macrova.aifoodsuggest.dto.*;
import org.karthick.macrova.aifoodsuggest.entity.*;
import org.karthick.macrova.aifoodsuggest.repository.*;
import org.karthick.macrova.dietplan.service.DietPlanService;
import org.karthick.macrova.exception.AIFoodSuggestErrorResponse;
import org.karthick.macrova.exception.BadGatewayException;
import org.karthick.macrova.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AIFoodSuggestService {

  // repositories
  private final AIFoodSuggestRepository aifoodSuggestRepository;
  private final AIFoodSuggestResponseRepository aiFoodSuggestResponseRepository;

  // services
  private final DietPlanService dietPlanService;
  private final LLMService llmService;

  public AIFoodSuggestData suggestFood(FoodPreferenceFilter filter) {
    String prompt = AIPrompts.getFoodSuggestUserPrompt(
      // TODO: find suggest count and macro targets
      convertToFoodPreference(
        filter,
        5,
        // TODO: change the type from long to int
        (int) dietPlanService.getCaloriesByMealCategory(filter.getMeal()),
        40,
        35,
        25
      )
    );
    AIFoodSuggestResponse aiResponse = creaAIFoodSuggestResponse(prompt);
    try {
      return suggestFood(aiResponse);
    } catch (BadGatewayException e) {
      return retryFoodSuggest(aiResponse);
    }
  }

  public AIFoodSuggestData retryFoodSuggest(String responseId) {
    Optional<AIFoodSuggestResponse> optionalResponse =
      aiFoodSuggestResponseRepository.findById(
        Objects.requireNonNull(responseId)
      );

    if (optionalResponse.isEmpty()) {
      throw new EntityNotFoundException("No AI response found.");
    }

    return retryFoodSuggest(optionalResponse.get());
  }

  public List<AIFoodSuggest> addSuggestions(
    List<AIFoodSuggest> aiFoodSuggests
  ) {
    return aifoodSuggestRepository.saveAll(
      Objects.requireNonNull(aiFoodSuggests)
    );
  }

  private AIFoodSuggestResponse creaAIFoodSuggestResponse(String prompt) {
    return aiFoodSuggestResponseRepository.save(
      new AIFoodSuggestResponse(prompt)
    );
  }

  private AIFoodSuggestData retryFoodSuggest(AIFoodSuggestResponse response) {
    return suggestFood(
      creaAIFoodSuggestResponse(
        AIPrompts.getRetryUserPrompt(response.getAiResponse())
      )
    );
  }

  private AIFoodSuggestData suggestFood(
    AIFoodSuggestResponse aiFoodSuggestResponse
  ) {
    try {
      String aiResponse = llmService.foodSuggestChat(
        aiFoodSuggestResponse.getPrompt()
      );
      aiFoodSuggestResponse.setAiResponse(aiResponse);
      List<AIFoodSuggest> aiFoodSuggests = addSuggestions(
        convertToFoodSuggest(aiResponse)
      );
      aiFoodSuggestResponse.setAiFoodSuggestIds(
        aiFoodSuggests
          .stream()
          .map(suggest -> suggest.getId())
          .toList()
      );
      return new AIFoodSuggestData(
        aiFoodSuggestResponse.getId(),
        aiFoodSuggests
      );
    } catch (Exception e) {
      aiFoodSuggestResponse.setErrorResponse(e.getMessage());
      throw new BadGatewayException(
        new AIFoodSuggestErrorResponse(
          aiFoodSuggestResponse.getId(),
          e.getMessage()
        )
      );
    } finally {
      aiFoodSuggestResponseRepository.save(
        Objects.requireNonNull(aiFoodSuggestResponse)
      );
    }
  }

  private List<AIFoodSuggest> convertToFoodSuggest(String response)
    throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(response, new TypeReference<>() {});
  }

  private FoodPreference convertToFoodPreference(
    FoodPreferenceFilter filter,
    int suggestCount,
    int calories,
    int proteinTarget,
    int carbsTarget,
    int fatTarget
  ) {
    return FoodPreference.builder()
      .foodPreferenceFilter(filter)
      .suggestCount(suggestCount)
      .calories(calories)
      .proteinTarget(proteinTarget)
      .carbsTarget(carbsTarget)
      .fatTarget(fatTarget)
      .build();
  }
}
