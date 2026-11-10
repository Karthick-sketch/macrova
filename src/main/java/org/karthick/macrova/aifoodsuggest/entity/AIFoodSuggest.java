package org.karthick.macrova.aifoodsuggest.entity;

import lombok.Data;
import org.karthick.macrova.shared.model.Calories;
import org.karthick.macrova.shared.model.Macros;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "ai_food_suggests")
public class AIFoodSuggest {

  private @Id String id;
  private String name;
  private Calories calories;
  private Macros macronutrients;
  private String description;
}
