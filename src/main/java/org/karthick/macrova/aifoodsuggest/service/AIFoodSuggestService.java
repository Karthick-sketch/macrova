package org.karthick.macrova.aifoodsuggest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.AllArgsConstructor;
import org.karthick.macrova.aichatservice.LLMService;
import org.karthick.macrova.aifoodsuggest.dto.FilterDTO;
import org.karthick.macrova.aifoodsuggest.entity.AIFoodSuggest;
import org.karthick.macrova.aifoodsuggest.repository.AIFoodSuggestRepository;
import org.karthick.macrova.dietplan.service.DietPlanService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AIFoodSuggestService {

  private final AIFoodSuggestRepository aifoodSuggestRepository;
  private final DietPlanService dietPlanService;
  private final LLMService llmService;

  private String buildPrompt(FilterDTO filter, long calories) {
    return String.format(
      "Suggest me 5 healthy %s cuisine %s foods for %s under %d calories.",
      filter.getCuisineFilter(),
      filter.getFoodFilter(),
      filter.getMealFilter(),
      calories
    );
  }

  public List<AIFoodSuggest> suggestFood(FilterDTO filterDTO)
    throws JsonProcessingException {
    return convertToFoodSuggest(
      llmService.foodSuggestChat(
        buildPrompt(
          filterDTO,
          dietPlanService.getCaloriesByMealCategory(filterDTO.getMealFilter())
        )
      )
    );
  }

  private List<AIFoodSuggest> convertToFoodSuggest(String response)
    throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(response, new TypeReference<>() {});
  }

  public void addSuggestions(@NonNull List<AIFoodSuggest> aiFoodSuggests) {
    aifoodSuggestRepository.saveAll(aiFoodSuggests);
  }
}
