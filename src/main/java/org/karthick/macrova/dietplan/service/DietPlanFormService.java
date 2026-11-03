package org.karthick.macrova.dietplan.service;

import java.time.LocalDate;
import java.util.Objects;
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
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DietPlanFormService {

  private final DietPlanRepository dietPlanRepository;

  private final UserSession userSession;

  public DietPlanFormStatus getDietPlanFormStatus() {
    DietPlanFormStatus dietPlanFormStatus = new DietPlanFormStatus();
    Optional<DietPlan> optionalDietPlan =
      dietPlanRepository.findByUserIdAndPlanStatus(
        userSession.getAuthenticatedUserId(),
        PlanStatus.ACTIVE
      );
    DietPlan dietPlan = null;
    if (optionalDietPlan.isPresent()) {
      dietPlan = optionalDietPlan.get();
      dietPlanFormStatus.setDietPlan(dietPlan);
    } else {
      dietPlan = getPendingDietPlan();
      if (FormStatus.GOAL_SELECTION.equals(dietPlan.getFormStatus())) {
        dietPlanFormStatus.setBodyMetrics(toBodyMetricsResponse(dietPlan));
      } else if (FormStatus.MEAL_PREFERENCE.equals(dietPlan.getFormStatus())) {
        dietPlanFormStatus.setCaloriesEstimation(
          toCaloriesEstimationResponse(
            dietPlan.getTdee(),
            dietPlan.getTargetCalories()
          )
        );
      } else {
        dietPlanFormStatus.setDietPlan(dietPlan);
      }
    }
    dietPlanFormStatus.setFormStatus(dietPlan.getFormStatus());
    dietPlanFormStatus.setPlanStatus(dietPlan.getPlanStatus());
    return dietPlanFormStatus;
  }

  public BodyMetricsResponse createDietPlanBodyMetrics(
    BodyMetricsRequest request
  ) {
    DietPlan dietPlan = toDietPlan(request);
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
    return toCaloriesEstimationResponse(
      dietPlan.getTdee(),
      dietPlan.getTargetCalories()
    );
  }

  public DietPlan createDietPlanMealPreference(MealPreferenceRequest request) {
    DietPlan dietPlan = getPendingDietPlan();
    dietPlan.setDietTypes(request.getDietType());
    dietPlan.setMealTypes(request.getMealType());
    dietPlan.setMacros(request.getMacros());
    dietPlan.setMealCount(request.getMealCount());
    dietPlan.setEveningSnack(request.isEveningSnack());
    dietPlan.setFormStatus(FormStatus.PREVIEW);
    return dietPlanRepository.save(dietPlan);
  }

  public DietPlan createDietPlan() {
    DietPlan dietPlan = getPendingDietPlan();
    dietPlan.setCreatedAt(LocalDate.now());
    dietPlan.setPlanStatus(PlanStatus.ACTIVE);
    dietPlan.setFormStatus(FormStatus.COMPLETED);
    return dietPlanRepository.save(dietPlan);
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

  private @NonNull DietPlan toDietPlan(BodyMetricsRequest request) {
    return Objects.requireNonNull(
      DietPlan.builder()
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
        .build()
    );
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
    double tdee,
    double targetCalories
  ) {
    return CaloriesEstimationResponse.builder()
      .tdee(tdee)
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
