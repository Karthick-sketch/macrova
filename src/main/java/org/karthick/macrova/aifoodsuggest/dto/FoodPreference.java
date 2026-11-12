package org.karthick.macrova.aifoodsuggest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodPreference {

  private FoodPreferenceFilter foodPreferenceFilter;
  private int suggestCount;
  private int calories;
  private int proteinTarget;
  private int carbsTarget;
  private int fatTarget;
}
