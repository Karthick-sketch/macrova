package org.karthick.macrova.dietplan.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import org.karthick.macrova.dietplan.dto.*;
import org.karthick.macrova.dietplan.entity.*;
import org.karthick.macrova.dietplan.service.DietPlanService;
import org.karthick.macrova.exception.BadRequestException;
import org.karthick.macrova.shared.model.Macros;
import org.karthick.macrova.shared.model.MealKcal;
import org.karthick.macrova.util.constants.MacrosConstants;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/diet-plan")
public class DietPlanController {

  private final DietPlanService dietPlanService;

  @GetMapping("/all")
  public List<DietPlan> getAllDietPlans() {
    return dietPlanService.findAllDietPlans();
  }

  @GetMapping("/history")
  public List<DietPlansHistoryDTO> getDietPlansHistory() {
    return dietPlanService.findDietPlansHistory();
  }

  @GetMapping
  public DietPlan getDietPlan() {
    return dietPlanService.findDietPlanByUserId();
  }

  @GetMapping("/{dietPlanId}")
  public DietPlan getDietPlanById(@PathVariable String dietPlanId) {
    return dietPlanService.findDietPlanById(dietPlanId);
  }

  @PostMapping("/tdee")
  public BodyMetricsResponse calculateTDEE(@RequestBody BodyMetricsRequest bodyMetricsDTO) {
    return dietPlanService.calculateTDEE(bodyMetricsDTO);
  }

  @PostMapping
  public DietPlan createDietPlan(@RequestBody DietPlan dietPlan) {
    return dietPlanService.createDietPlan(dietPlan);
  }

  @PutMapping("/update")
  public DietPlan updateDietPlan(@RequestBody DietPlan dietPlan) {
    return dietPlanService.updateDietPlan(dietPlan);
  }

  @PostMapping("/add-weight/{dietPlanId}")
  public DietPlanTrack addWeight(
      @PathVariable String dietPlanId,
      @RequestBody double weight) {
    return dietPlanService.addWeight(dietPlanId, weight);
  }

  @GetMapping("/track/{dietPlanTrackId}")
  public DietPlanTrack getDietPlanTrackById(
      @PathVariable @NonNull String dietPlanTrackId) {
    return dietPlanService.findDietPlanTrackById(dietPlanTrackId);
  }

  @GetMapping("/track/plan/{dietPlanId}")
  public DietPlanTrack getDietPlanTrackByDietPlanId(
      @PathVariable String dietPlanId) {
    return dietPlanService.findDietPlanTrackByDietPlanId(dietPlanId);
  }

  @PostMapping("/{category}/{dietPlanId}")
  public DietPlanTrack updateMacros(
      @PathVariable String category,
      @PathVariable String dietPlanId,
      @RequestBody Macros macros) {
    if (MacrosConstants.validateMacro(category)) {
      return dietPlanService.updateMacros(dietPlanId, category, macros);
    }
    throw new BadRequestException("Invalid macro category");
  }

  @GetMapping("/meal-kcal/{id}")
  public MealKcal getMealKcal(@PathVariable("id") String id) {
    return this.dietPlanService.getMealKcal(id);
  }

  @GetMapping("/metrics")
  public MetricsDTO getMetricsByDateRange() {
    return dietPlanService.getMetricsByDateRange();
  }

  @GetMapping("/active-plans")
  public boolean isThereAnyActivePlans() {
    return dietPlanService.isThereAnyActivePlans();
  }

  @GetMapping("/overview/{dietPlanId}")
  public List<DietPlanOverviewDTO> getDietPlansOverview(
      @PathVariable String dietPlanId) {
    return dietPlanService.getDietPlansOverview(dietPlanId);
  }

  @GetMapping("/status")
  public boolean isDietPlanReachedDuration() {
    return dietPlanService.isDietPlanReachedDuration();
  }

  @GetMapping("/past-plan")
  public DietPlansHistoryDTO getPastPlan() {
    return dietPlanService.findPastDietPlan();
  }
}
