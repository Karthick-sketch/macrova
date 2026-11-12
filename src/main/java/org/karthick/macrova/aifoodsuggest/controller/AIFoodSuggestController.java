package org.karthick.macrova.aifoodsuggest.controller;

import lombok.AllArgsConstructor;
import org.karthick.macrova.aifoodsuggest.dto.*;
import org.karthick.macrova.aifoodsuggest.service.AIFoodSuggestService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai-food-suggest")
@AllArgsConstructor
public class AIFoodSuggestController {

  private final AIFoodSuggestService aiFoodSuggestService;

  @PostMapping
  public AIFoodSuggestData suggestFood(
    @RequestBody FoodPreferenceFilter filter
  ) {
    return aiFoodSuggestService.suggestFood(filter);
  }

  @PostMapping("/retry/{responseId}")
  public AIFoodSuggestData retryFoodSuggest(@PathVariable String responseId) {
    return aiFoodSuggestService.retryFoodSuggest(responseId);
  }
}
