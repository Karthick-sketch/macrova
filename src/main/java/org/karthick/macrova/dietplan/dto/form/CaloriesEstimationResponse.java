package org.karthick.macrova.dietplan.dto.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.karthick.macrova.shared.model.MacroPreference;
import org.karthick.macrova.shared.model.MacroQuantity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaloriesEstimationResponse {

  private double tdee;
  private double targetCalories;
  private MacroQuantity proteinQuantity;
  private MacroQuantity carbsQuantity;
  private MacroQuantity fatQuantity;
  private MacroPreference balanced;
  private MacroPreference highProtein;
  private MacroPreference lowCarb;
  private MacroPreference lowFat;
}
