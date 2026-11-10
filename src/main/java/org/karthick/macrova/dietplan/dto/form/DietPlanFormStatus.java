package org.karthick.macrova.dietplan.dto.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.karthick.macrova.dietplan.entity.DietPlan;
import org.karthick.macrova.dietplan.enums.FormStatus;
import org.karthick.macrova.dietplan.enums.PlanStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietPlanFormStatus {

  private PlanStatus planStatus;
  private FormStatus formStatus;
  private DietPlan dietPlan;
  private BodyMetricsRequest bodyMetricsRequest;
  private BodyMetricsResponse bodyMetricsResponse;
  private CaloriesEstimationResponse caloriesEstimation;
}
