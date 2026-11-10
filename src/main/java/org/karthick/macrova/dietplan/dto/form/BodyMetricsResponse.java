package org.karthick.macrova.dietplan.dto.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.karthick.macrova.dietplan.enums.Plan;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyMetricsResponse {

  private double tdee;
  private double weight;
  private double targetWeight;
  private Plan plan;
}
