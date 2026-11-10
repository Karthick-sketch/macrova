package org.karthick.macrova.dietplan.dto.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.karthick.macrova.shared.model.MacrosPreference;
import org.karthick.macrova.shared.model.MacrosQuantity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaloriesEstimationResponse {

  private double tdee;
  private double targetCalories;
  private MacrosQuantity proteinQuantity;
  private MacrosQuantity carbsQuantity;
  private MacrosQuantity fatQuantity;
  private MacrosPreference macrosPreference;
}
