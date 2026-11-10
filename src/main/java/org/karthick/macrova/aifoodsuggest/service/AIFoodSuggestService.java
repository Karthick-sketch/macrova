package org.karthick.macrova.aifoodsuggest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.karthick.macrova.aichatservice.LLMService;
import org.karthick.macrova.aifoodsuggest.dto.FilterDTO;
import org.karthick.macrova.aifoodsuggest.entity.*;
import org.karthick.macrova.aifoodsuggest.repository.*;
import org.karthick.macrova.dietplan.service.DietPlanService;
import org.karthick.macrova.exception.BadGatewayException;
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

  private String buildPrompt(FilterDTO filter, long calories) {
    return String.format(
      "Suggest me 5 healthy %s cuisine %s %s foods for %s under %d calories.",
      filter.getCuisineFilter(),
      filter.getMealTypeFilter(),
      filter.getDietTypeFilter(),
      filter.getMealFilter(),
      calories
    );
  }

  public List<AIFoodSuggest> suggestFood(FilterDTO filterDTO) {
    String prompt = buildPrompt(
      filterDTO,
      dietPlanService.getCaloriesByMealCategory(filterDTO.getMealFilter())
    );
    AIFoodSuggestResponse aiFoodSuggestResponse = new AIFoodSuggestResponse(
      prompt
    );
    try {
      String aiResponse = llmService.foodSuggestChat(prompt);
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
      return aiFoodSuggests;
    } catch (Exception e) {
      aiFoodSuggestResponse.setErrorResponse(e.getMessage());
      throw new BadGatewayException("Problem with LLM");
    } finally {
      aiFoodSuggestResponseRepository.save(aiFoodSuggestResponse);
    }
  }

  public List<AIFoodSuggest> addSuggestions(
    List<AIFoodSuggest> aiFoodSuggests
  ) {
    return aifoodSuggestRepository.saveAll(
      Objects.requireNonNull(aiFoodSuggests)
    );
  }

  private List<AIFoodSuggest> convertToFoodSuggest(String response)
    throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(response, new TypeReference<>() {});
  }
}
