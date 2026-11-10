package org.karthick.macrova.dietplan.dto.form;

import lombok.Data;
import org.karthick.macrova.dietplan.enums.DietType;
import org.karthick.macrova.dietplan.enums.MealType;
import org.karthick.macrova.shared.model.Macros;

@Data
public class MealPreferenceRequest {

  private double currentCalories;
  private Macros macros;
  private DietType dietType;
  private MealType mealType;
  private int mealCount;
  private boolean eveningSnack;
}
