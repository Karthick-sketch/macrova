package org.karthick.macrova.aifoodsuggest.dto;

import lombok.Data;

@Data
public class FoodPreferenceFilter {

  private String meal;
  private String mealType;
  private String dietType;
  private String cuisine;
}
