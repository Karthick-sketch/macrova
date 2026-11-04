package org.karthick.macrova.dietplan.service;

import java.time.LocalDate;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.karthick.macrova.dietplan.dto.form.*;
import org.karthick.macrova.dietplan.entity.DietPlan;
import org.karthick.macrova.dietplan.enums.FormStatus;
import org.karthick.macrova.dietplan.enums.Plan;
import org.karthick.macrova.dietplan.enums.PlanStatus;
import org.karthick.macrova.dietplan.repository.DietPlanRepository;
import org.karthick.macrova.exception.BadRequestException;
import org.karthick.macrova.security.UserSession;
import org.karthick.macrova.shared.model.MacrosPreference;
import org.karthick.macrova.util.NutrientsCalculator;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DietPlanFormService {

  private final DietPlanRepository dietPlanRepository;

  private final DietPlanService dietPlanService;

  private final UserSession userSession;

  public DietPlanFormStatus getDietPlanFormStatus() {
    return getDietPlanFormStatus(null);
  }

  public DietPlanFormStatus getDietPlanFormDetails(CurrentFormStatus status) {
    return getDietPlanFormStatus(status.getFormStatus());
  }

  public BodyMetricsResponse createDietPlanBodyMetrics(
    BodyMetricsRequest request
  ) {
    Optional<DietPlan> optionalDietPlan =
      dietPlanRepository.findByUserIdAndPlanStatus(
        userSession.getAuthenticatedUserId(),
        PlanStatus.PENDING
      );
    DietPlan dietPlan = toDietPlan(request);
    optionalDietPlan.ifPresent(d -> dietPlan.setId(d.getId()));
    dietPlan.setTdee(
      Math.round(
        NutrientsCalculator.findTDEE(
          dietPlan.getAge(),
          dietPlan.getWeight(),
          dietPlan.getHeight(),
          dietPlan.getGender(),
          dietPlan.getActivity()
        )
      )
    );
    dietPlan.setFormStatus(FormStatus.GOAL_SELECTION);
    return toBodyMetricsResponse(dietPlanRepository.save(dietPlan));
  }

  public CaloriesEstimationResponse createDietPlanGoalSelection(
    GoalSelectionRequest request
  ) {
    DietPlan dietPlan = getPendingDietPlan();
    dietPlan.setGoal(request.getGoal());
    dietPlan.setDuration(request.getDuration());
    dietPlan.setFormStatus(FormStatus.MEAL_PREFERENCE);

    if (Plan.WEIGHT_LOSS.equals(dietPlan.getPlan())) {
      dietPlan.setTargetCalories(
        NutrientsCalculator.findDeficit(
          dietPlan.getTdee(),
          dietPlan.getGoal().getValue()
        )
      );
    } else if (Plan.WEIGHT_GAIN.equals(dietPlan.getPlan())) {
      dietPlan.setTargetCalories(
        NutrientsCalculator.findSurplus(
          dietPlan.getTdee(),
          dietPlan.getGoal().getValue()
        )
      );
    } else {
      throw new BadRequestException("Invalid Diet plan");
    }

    dietPlan = dietPlanRepository.save(dietPlan);
    return toCaloriesEstimationResponse(dietPlan);
  }

  public DietPlan createDietPlanMealPreference(MealPreferenceRequest request) {
    DietPlan dietPlan = getPendingDietPlan();
    dietPlan.setCurrentCalories(request.getCurrentCalories());
    dietPlan.setMacros(request.getMacros());
    dietPlan.setDietType(request.getDietType());
    dietPlan.setMealType(request.getMealType());
    dietPlan.setMealCount(request.getMealCount());
    dietPlan.setEveningSnack(request.isEveningSnack());
    dietPlan.setFormStatus(FormStatus.PREVIEW);
    return dietPlanRepository.save(dietPlan);
  }

  public DietPlan createDietPlan() {
    DietPlan dietPlan = getPendingDietPlan();

    // TODO: remove currentCalories logic, directly use targetCalories
    dietPlan.setTargetCalories(dietPlan.getCurrentCalories());

    dietPlan.setTodayWeight(dietPlan.getWeight());
    dietPlan.setCreatedAt(LocalDate.now());
    dietPlan.setPlanStatus(PlanStatus.ACTIVE);
    dietPlan.setFormStatus(FormStatus.COMPLETED);
    dietPlan = dietPlanRepository.save(dietPlan);
    dietPlanService.createDietPlanTrack(dietPlan);
    return dietPlan;
  }

  private DietPlan getPendingDietPlan() {
    Optional<DietPlan> optionalDietPlan =
      dietPlanRepository.findByUserIdAndPlanStatus(
        userSession.getAuthenticatedUserId(),
        PlanStatus.PENDING
      );
    if (optionalDietPlan.isEmpty()) {
      throw new BadRequestException("No pending diet plan found.");
    }

    return optionalDietPlan.get();
  }

  private DietPlanFormStatus getDietPlanFormStatus(
    @Nullable FormStatus status
  ) {
    Optional<DietPlan> optionalDietPlan =
      dietPlanRepository.findByUserIdAndPlanStatus(
        userSession.getAuthenticatedUserId(),
        PlanStatus.ACTIVE
      );
    DietPlanFormStatus dietPlanFormStatus = new DietPlanFormStatus();
    DietPlan dietPlan = null;
    if (optionalDietPlan.isPresent()) {
      dietPlan = optionalDietPlan.get();
      dietPlanFormStatus.setDietPlan(dietPlan);
    } else {
      dietPlan = getPendingDietPlan();

      // If status is null, set it to the diet plan's form status
      if (status == null) {
        status = dietPlan.getFormStatus();
      }

      if (FormStatus.BODY_METRICS.equals(status)) {
        dietPlanFormStatus.setBodyMetricsRequest(
          toBodyMetricsRequest(dietPlan)
        );
      } else if (FormStatus.GOAL_SELECTION.equals(status)) {
        dietPlanFormStatus.setBodyMetricsResponse(
          toBodyMetricsResponse(dietPlan)
        );
      } else if (FormStatus.MEAL_PREFERENCE.equals(status)) {
        dietPlanFormStatus.setCaloriesEstimation(
          toCaloriesEstimationResponse(dietPlan)
        );
      } else {
        dietPlanFormStatus.setDietPlan(dietPlan);
      }
    }
    dietPlanFormStatus.setFormStatus(dietPlan.getFormStatus());
    dietPlanFormStatus.setPlanStatus(dietPlan.getPlanStatus());
    return dietPlanFormStatus;
  }

  private DietPlan toDietPlan(BodyMetricsRequest request) {
    return DietPlan.builder()
      .title(request.getTitle())
      .plan(request.getPlan())
      .gender(request.getGender())
      .age(request.getAge())
      .height(request.getHeight())
      .weight(request.getWeight())
      .todayWeight(request.getWeight())
      .targetWeight(request.getTargetWeight())
      .activity(request.getActivity())
      .userId(userSession.getAuthenticatedUserId())
      .build();
  }

  private BodyMetricsRequest toBodyMetricsRequest(DietPlan dietPlan) {
    return BodyMetricsRequest.builder()
      .title(dietPlan.getTitle())
      .plan(dietPlan.getPlan())
      .gender(dietPlan.getGender())
      .age(dietPlan.getAge())
      .height(dietPlan.getHeight())
      .weight(dietPlan.getWeight())
      .targetWeight(dietPlan.getTargetWeight())
      .activity(dietPlan.getActivity())
      .build();
  }

  private BodyMetricsResponse toBodyMetricsResponse(DietPlan dietPlan) {
    return BodyMetricsResponse.builder()
      .tdee(dietPlan.getTdee())
      .weight(dietPlan.getWeight())
      .targetWeight(dietPlan.getTargetWeight())
      .plan(dietPlan.getPlan())
      .build();
  }

  private CaloriesEstimationResponse toCaloriesEstimationResponse(
    DietPlan dietPlan
  ) {
    double targetCalories = dietPlan.getTargetCalories();
    return CaloriesEstimationResponse.builder()
      .tdee(dietPlan.getTdee())
      .targetCalories(targetCalories)
      .proteinQuantity(NutrientsCalculator.findProteinQuantity(targetCalories))
      .carbsQuantity(NutrientsCalculator.findCarbsQuantity(targetCalories))
      .fatQuantity(NutrientsCalculator.findFatQuantity(targetCalories))
      .macrosPreference(toMacrosPreference(targetCalories))
      .build();
  }

  private MacrosPreference toMacrosPreference(double targetCalories) {
    return MacrosPreference.builder()
      .balanced(NutrientsCalculator.findBalancedMacros(targetCalories))
      .highProtein(NutrientsCalculator.findHighProteinMacros(targetCalories))
      .lowCarb(NutrientsCalculator.findLowCarbMacros(targetCalories))
      .lowFat(NutrientsCalculator.findLowFatMacros(targetCalories))
      .build();
  }
}
