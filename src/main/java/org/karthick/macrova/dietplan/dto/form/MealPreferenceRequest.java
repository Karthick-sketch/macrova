package org.karthick.macrova.dietplan.dto.form;

import lombok.Data;
import org.karthick.macrova.dietplan.enums.DietTypes;
import org.karthick.macrova.dietplan.enums.MealTypes;
import org.karthick.macrova.shared.model.Macros;

@Data
public class MealPreferenceRequest {

  private Macros macros;
  private DietTypes dietType;
  private MealTypes mealType;
  private int mealCount;
  private boolean eveningSnack;
}
