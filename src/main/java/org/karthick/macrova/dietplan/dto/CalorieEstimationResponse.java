package org.karthick.macrova.dietplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalorieEstimationResponse {

  private double tdee;
  private double calorieDeficit;
  private MacrosQuantity proteinQuantity;
  private MacrosQuantity carbsQuantity;
  private MacrosQuantity fatQuantity;
  private MacrosPreference balanced;
  private MacrosPreference highProtein;
  private MacrosPreference lowCarb;
  private MacrosPreference lowFat;
}
