package org.karthick.macrova.dietplan.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import lombok.AllArgsConstructor;
import org.karthick.macrova.dietplan.dto.*;
import org.karthick.macrova.dietplan.entity.*;
import org.karthick.macrova.dietplan.enums.Goal;
import org.karthick.macrova.dietplan.enums.Plan;
import org.karthick.macrova.dietplan.repository.*;
import org.karthick.macrova.exception.BadRequestException;
import org.karthick.macrova.exception.EntityNotFoundException;
import org.karthick.macrova.security.UserSession;
import org.karthick.macrova.shared.model.Calories;
import org.karthick.macrova.shared.model.Macros;
import org.karthick.macrova.shared.model.MealKcal;
import org.karthick.macrova.util.NutrientsCalculator;
import org.karthick.macrova.util.constants.MacrosConstants;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DietPlanService {

  private final DietPlanRepository dietPlannerRepository;
  private final DietPlanTrackRepository dietPlanTrackRepository;

  private final UserSession userSession;

  public List<DietPlan> findAllDietPlans() {
    return dietPlannerRepository.findByUserId(
        userSession.getAuthenticatedUserId());
  }

  public List<DietPlansHistoryDTO> findDietPlansHistory() {
    return dietPlannerRepository.findDietPlansHistoryByUserId(
        userSession.getAuthenticatedUserId());
  }

  public DietPlan findDietPlanById(String id) {
    Optional<DietPlan> dietPlan = dietPlannerRepository.findByIdAndUserId(
        id,
        userSession.getAuthenticatedUserId());
    if (dietPlan.isEmpty()) {
      throw new EntityNotFoundException("Diet plan not found");
    }
    return dietPlan.get();
  }

  public DietPlan findDietPlanByUserId() {
    Optional<DietPlan> dietPlan = dietPlannerRepository.findByUserIdAndActiveIsTrue(
        userSession.getAuthenticatedUserId());
    if (dietPlan.isEmpty()) {
      throw new EntityNotFoundException("Diet plan not found");
    }
    return dietPlan.get();
  }

  public BodyMetricsResponse calculateTDEE(BodyMetricsRequest bodyMetricsDTO) {
    double tdee = Math.round(
        NutrientsCalculator.findTDEE(
            bodyMetricsDTO.getAge(),
            bodyMetricsDTO.getWeight(),
            bodyMetricsDTO.getHeight(),
            bodyMetricsDTO.getGender(),
            bodyMetricsDTO.getActivity()));
    BodyMetricsResponse res = toBodyMetricsResponse(bodyMetricsDTO);
    res.setTdee(tdee);
    return res;
  }

  public DietPlan createDietPlan(DietPlan dietPlan) {
    validateDietPlan(dietPlan);
    dietPlan.setTodayWeight(dietPlan.getWeight());
    dietPlan.setUserId(userSession.getAuthenticatedUserId());
    dietPlan.setActive(true);
    dietPlannerRepository.save(dietPlan);
    createDietPlanTrack(dietPlan);
    return dietPlan;
  }

  public DietPlan updateDietPlan(DietPlan dietPlan) {
    return createDietPlan(dietPlan);
  }

  private void validateDietPlan(DietPlan dietPlan) {
    // validate Target weight
    double bmi = NutrientsCalculator.findBMI(
        dietPlan.getHeight(),
        dietPlan.getTargetWeight());
    if ((Plan.WEIGHT_LOSS.equals(dietPlan.getPlan()) && bmi < 18) ||
        (Plan.WEIGHT_GAIN.equals(dietPlan.getPlan()) && bmi > 25)) {
      throw new BadRequestException(
          dietPlan.getTargetWeight() + "kg is not the healthy weight");
    }
    // validate Duration
    double mass = dietPlan.getWeight() - dietPlan.getTargetWeight();
    int min = NutrientsCalculator.findMinimumDuration(mass, dietPlan.getPlan());
    int max = NutrientsCalculator.findMaximumDuration(mass);
    if (dietPlan.getDuration() < min || dietPlan.getDuration() > max) {
      throw new BadRequestException(
          min + "-" + max + " days are the healthy duration");
    }
  }

  private void processDietPlan(DietPlan dietPlan, DietPlanTrack dietPlanTrack) {
    dietPlanTrack.setTdee(
        NutrientsCalculator.findTDEE(
            dietPlan.getAge(),
            dietPlanTrack.getWeight(),
            dietPlan.getHeight(),
            dietPlan.getGender(),
            dietPlan.getActivity()));
    double intake = getIntake(
        dietPlanTrack.getTdee(),
        dietPlan.getGoal(),
        dietPlan.getPlan());
    dietPlanTrack.setIntake(new Calories(intake));
    dietPlanTrack.setProtein(
        new Calories(NutrientsCalculator.findProtein(intake)));
    dietPlanTrack.setFat(new Calories(NutrientsCalculator.findFat(intake)));
    dietPlanTrack.setCarbs(new Calories(NutrientsCalculator.findCarbs(intake)));
    dietPlanTrack.setMealKcal(splitForMealKcal(dietPlanTrack));
  }

  private double getIntake(double tdee, Goal goal, Plan plan) {
    return plan == Plan.WEIGHT_LOSS
        ? NutrientsCalculator.findDeficit(tdee, goal.getValue())
        : NutrientsCalculator.findSurplus(tdee, goal.getValue());
  }

  public DietPlanTrack createDietPlanTrack(DietPlan dietPlan) {
    LocalDate today = LocalDate.now();
    int dayCount = countDays(dietPlan.getCreatedAt(), today);
    DietPlanTrack dietPlanTrack = new DietPlanTrack(
        dietPlan.getTodayWeight(),
        today,
        dayCount,
        dietPlan.getId());
    processDietPlan(dietPlan, dietPlanTrack);
    return dietPlanTrackRepository.save(dietPlanTrack);
  }

  private int countDays(LocalDate startDate, LocalDate endDate) {
    return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
  }

  public DietPlanTrack findDietPlanTrackById(@NonNull String dietPlanTrackId) {
    Optional<DietPlanTrack> dietPlanTrack = dietPlanTrackRepository.findById(
        dietPlanTrackId);
    if (dietPlanTrack.isEmpty()) {
      throw new EntityNotFoundException("Diet plan track not found");
    }
    return dietPlanTrack.get();
  }

  public DietPlanTrack findDietPlanTrackByDietPlanId(String dietPlanId) {
    DietPlan dietPlan = findDietPlanById(dietPlanId);
    return dietPlanTrackRepository
        .findFirstByCreatedAtAndDietPlanId(LocalDate.now(), dietPlanId)
        .orElseGet(() -> createDietPlanTrack(dietPlan));
  }

  public DietPlanTrack addWeight(String dietPlanId, double weight) {
    DietPlan dietPlan = findDietPlanById(dietPlanId);
    dietPlan.setTodayWeight(weight);
    dietPlannerRepository.save(dietPlan);
    DietPlanTrack dietPlanTrack = findDietPlanTrackByDietPlanId(dietPlanId);
    dietPlanTrack.setWeight(weight);
    processDietPlan(dietPlan, dietPlanTrack);
    return dietPlanTrackRepository.save(dietPlanTrack);
  }

  public DietPlanTrack updateMacros(
      String dietPlanId,
      String category,
      Macros macros) {
    DietPlanTrack dietPlanTrack = findDietPlanTrackByDietPlanId(dietPlanId);
    Macros dietPlanMacros = getMacrosByCategory(
        dietPlanTrack.getMealKcal(),
        category);
    dietPlanMacros.getProtein().setTaken(macros.getProtein().getTaken());
    dietPlanMacros.getFat().setTaken(macros.getFat().getTaken());
    dietPlanMacros.getCarbs().setTaken(macros.getCarbs().getTaken());
    adjustMacros(dietPlanTrack);
    dietPlanTrack.getIntake().setTaken(calcDeficitTaken(dietPlanTrack));
    return dietPlanTrackRepository.save(dietPlanTrack);
  }

  private Macros getMacrosByCategory(MealKcal mealKcal, String category) {
    return switch (category) {
      case MacrosConstants.BREAKFAST -> mealKcal.getBreakfast();
      case MacrosConstants.LUNCH -> mealKcal.getLunch();
      case MacrosConstants.DINNER -> mealKcal.getDinner();
      default -> mealKcal.getSnack();
    };
  }

  private double calcDeficitTaken(DietPlanTrack dietPlanTrack) {
    return NutrientsCalculator.calcTakenDeficit(
        new Macros(
            dietPlanTrack.getProtein(),
            dietPlanTrack.getFat(),
            dietPlanTrack.getCarbs()));
  }

  private void adjustMacros(DietPlanTrack dietPlanTrack) {
    MealKcal mealKcal = dietPlanTrack.getMealKcal();
    dietPlanTrack
        .getProtein()
        .setTaken(
            mealKcal.getBreakfast().getProtein().getTaken() +
                mealKcal.getLunch().getProtein().getTaken() +
                mealKcal.getDinner().getProtein().getTaken() +
                mealKcal.getSnack().getProtein().getTaken());
    dietPlanTrack
        .getCarbs()
        .setTaken(
            mealKcal.getBreakfast().getCarbs().getTaken() +
                mealKcal.getLunch().getCarbs().getTaken() +
                mealKcal.getDinner().getCarbs().getTaken() +
                mealKcal.getSnack().getCarbs().getTaken());
    dietPlanTrack
        .getFat()
        .setTaken(
            mealKcal.getBreakfast().getFat().getTaken() +
                mealKcal.getLunch().getFat().getTaken() +
                mealKcal.getDinner().getFat().getTaken() +
                mealKcal.getSnack().getFat().getTaken());
  }

  public MealKcal getMealKcal(String dietPlanId) {
    Optional<DietPlanTrack> dietPlanTrack = dietPlanTrackRepository.findFirstByCreatedAtAndDietPlanId(
        LocalDate.now(),
        dietPlanId);
    if (dietPlanTrack.isEmpty()) {
      throw new EntityNotFoundException("No macros found.");
    }
    return splitForMealKcal(dietPlanTrack.get());
  }

  private MealKcal splitForMealKcal(DietPlanTrack dietPlanTrack) {
    Macros macros = new Macros(
        dietPlanTrack.getProtein(),
        dietPlanTrack.getFat(),
        dietPlanTrack.getCarbs());
    return new MealKcal(
        NutrientsCalculator.macrosPercentage(macros, 30),
        NutrientsCalculator.macrosPercentage(macros, 30),
        NutrientsCalculator.macrosPercentage(macros, 15),
        NutrientsCalculator.macrosPercentage(macros, 25));
  }

  public MetricsDTO getMetricsByDateRange() {
    List<String> days = new ArrayList<>();
    List<Double> weights = new ArrayList<>();
    Date[] dateRange = getOneWeekDateRange();
    DietPlan dietPlan = findDietPlanByUserId();
    DietPlanTrack dietPlanTrack = findDietPlanTrackByDietPlanId(
        dietPlan.getId());
    dietPlanTrackRepository
        .findByDateRangeAndDietPlanId(
            dateRange[0],
            dateRange[1],
            dietPlan.getId())
        .forEach(track -> {
          days.add(getDayName(track.getCreatedAt()));
          weights.add(track.getWeight());
        });
    return buildMetrics(
        dietPlanTrack,
        dietPlan.getTitle(),
        dietPlan.getDuration(),
        days,
        weights);
  }

  private Date[] getOneWeekDateRange() {
    ZoneId zoneId = ZoneId.of("Asia/Kolkata");
    LocalDate today = LocalDate.now(zoneId);
    return new Date[] {
        Date.from(today.minusWeeks(1).atStartOfDay(zoneId).toInstant()),
        Date.from(today.atTime(LocalTime.MAX).atZone(zoneId).toInstant()),
    };
  }

  private String getDayName(LocalDate date) {
    return date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
  }

  private MetricsDTO buildMetrics(
      DietPlanTrack dietPlanTrack,
      String title,
      int duration,
      List<String> days,
      List<Double> weights) {
    Calories intake = dietPlanTrack.getIntake();
    return MetricsDTO.builder()
        .title(title)
        .taken(
            NutrientsCalculator.findPercentage(intake.getTaken(), intake.getTotal()))
        .duration(duration)
        .currentDay(dietPlanTrack.getDayCount())
        .currentWeight(dietPlanTrack.getWeight())
        .intake(intake.getTotal())
        .protein(dietPlanTrack.getProtein().getTotal())
        .carbs(dietPlanTrack.getCarbs().getTotal())
        .fat(dietPlanTrack.getFat().getTotal())
        .days(days)
        .weights(weights)
        .build();
  }

  public boolean isThereAnyActivePlans() {
    return dietPlannerRepository.findByActiveTrue().isPresent();
  }

  public long getCaloriesByMealCategory(String category) {
    try {
      DietPlan dietPlan = findDietPlanByUserId();
      DietPlanTrack dietPlanTrack = findDietPlanTrackByDietPlanId(
          dietPlan.getId());
      Macros macros = getMacrosByCategory(
          dietPlanTrack.getMealKcal(),
          category);
      return NutrientsCalculator.calcTotalDeficit(macros);
    } catch (EntityNotFoundException e) {
      return 500L;
    }
  }

  public List<DietPlanOverviewDTO> getDietPlansOverview(String dietPlanId) {
    return dietPlanTrackRepository.findAllDietPlanOverview(dietPlanId);
  }

  public boolean isDietPlanReachedDuration() {
    try {
      DietPlan dietPlan = findDietPlanByUserId();
      int dayCount = countDays(dietPlan.getCreatedAt(), LocalDate.now());
      if (dayCount > dietPlan.getDuration()) {
        dietPlan.setActive(false);
        dietPlannerRepository.save(dietPlan);
        return true;
      }
      return false;
    } catch (EntityNotFoundException ignored) {
      return true;
    }
  }

  public DietPlansHistoryDTO findPastDietPlan() {
    Optional<DietPlansHistoryDTO> dietPlan = dietPlannerRepository.findFirstDietPlansHistoryByUserId(
        userSession.getAuthenticatedUserId());
    if (dietPlan.isEmpty()) {
      throw new EntityNotFoundException("No past plan found.");
    }
    return dietPlan.get();
  }

  private BodyMetricsResponse toBodyMetricsResponse(BodyMetricsRequest request) {
    return BodyMetricsResponse.builder()
        .title(request.getTitle())
        .plan(request.getPlan())
        .gender(request.getGender())
        .age(request.getAge())
        .height(request.getHeight())
        .weight(request.getWeight())
        .targetWeight(request.getTargetWeight())
        .activity(request.getActivity())
        .build();
  }
}
