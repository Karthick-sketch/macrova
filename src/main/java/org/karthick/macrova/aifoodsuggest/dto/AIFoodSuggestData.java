package org.karthick.macrova.aifoodsuggest.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.karthick.macrova.aifoodsuggest.entity.AIFoodSuggest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIFoodSuggestData {

  private String aiResponseId;
  private List<AIFoodSuggest> aiFoodSuggests;
}
