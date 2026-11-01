package org.karthick.macrova.dietplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.karthick.macrova.dietplan.enums.Goal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalorieEstimationRequest {

  private double tdee;
  private Goal goal;
  private int duration;
}
