package org.karthick.macrova.dietplan.controller;

import lombok.AllArgsConstructor;
import org.karthick.macrova.dietplan.dto.form.*;
import org.karthick.macrova.dietplan.entity.DietPlan;
import org.karthick.macrova.dietplan.service.DietPlanFormService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/diet-plan/form")
@AllArgsConstructor
public class DietPlanFormController {

  private final DietPlanFormService dietPlanFormService;

  // Diet plan form related endpoints
  @GetMapping("/status")
  public DietPlanFormStatus getDietPlanFormStatus() {
    return dietPlanFormService.getDietPlanFormStatus();
  }

  @PostMapping("/status")
  public DietPlanFormStatus getDietPlanFormDetails(
    @RequestBody CurrentFormStatus status
  ) {
    return dietPlanFormService.getDietPlanFormDetails(status);
  }

  @PostMapping("/body-metrics")
  public BodyMetricsResponse createDietPlanBodyMetrics(
    @RequestBody BodyMetricsRequest request
  ) {
    return dietPlanFormService.createDietPlanBodyMetrics(request);
  }

  @PostMapping("/goal-selection")
  public CaloriesEstimationResponse createDietPlanGoalSelection(
    @RequestBody GoalSelectionRequest request
  ) {
    return dietPlanFormService.createDietPlanGoalSelection(request);
  }

  @PostMapping("/meal-preference")
  public DietPlan createDietPlanMealPreference(
    @RequestBody MealPreferenceRequest request
  ) {
    return dietPlanFormService.createDietPlanMealPreference(request);
  }

  @PostMapping("/create")
  public DietPlan createDietPlan() {
    return dietPlanFormService.createDietPlan();
  }
}
