package org.karthick.macrova.aifoodsuggest.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import org.karthick.macrova.aifoodsuggest.dto.FilterDTO;
import org.karthick.macrova.aifoodsuggest.entity.AIFoodSuggest;
import org.karthick.macrova.aifoodsuggest.service.AIFoodSuggestService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai-food-suggest")
@AllArgsConstructor
public class AIFoodSuggestController {

  private final AIFoodSuggestService aiFoodSuggestService;

  @PostMapping
  public List<AIFoodSuggest> suggestFood(@RequestBody FilterDTO filterDTO) {
    return aiFoodSuggestService.suggestFood(filterDTO);
  }

  @PostMapping("/retry/{responseId}")
  public List<AIFoodSuggest> retryFoodSuggest(@PathVariable String responseId) {
    return aiFoodSuggestService.retryFoodSuggest(responseId);
  }
}
