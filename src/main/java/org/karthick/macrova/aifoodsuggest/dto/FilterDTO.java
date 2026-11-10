package org.karthick.macrova.aifoodsuggest.dto;

import lombok.Data;

@Data
public class FilterDTO {

  private String mealFilter;
  private String mealTypeFilter;
  private String dietTypeFilter;
  private String cuisineFilter;
}
