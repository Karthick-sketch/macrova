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
import org.karthick.macrova.dietplan.enums.PlanStatus;
import org.karthick.macrova.dietplan.repository.*;
import org.karthick.macrova.exception.BadRequestException;
import org.karthick.macrova.exception.EntityNotFoundException;
import org.karthick.macrova.security.UserSession;
import org.karthick.macrova.shared.model.Calories;
import org.karthick.macrova.shared.model.Macros;
import org.karthick.macrova.shared.model.MacrosTrack;
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
      userSession.getAuthenticatedUserId()
    );
  }

  public List<DietPlanHistoryDTO> findDietPlanHistory() {
    return dietPlannerRepository.findDietPlanHistoryByUserId(
      userSession.getAuthenticatedUserId()
    );
  }

  public DietPlan findDietPlanById(String id) {
    Optional<DietPlan> dietPlan = dietPlannerRepository.findByIdAndUserId(
      id,
      userSession.getAuthenticatedUserId()
    );
    if (dietPlan.isEmpty()) {
      throw new EntityNotFoundException("Diet plan not found");
    }
    return dietPlan.get();
  }

  public DietPlan findDietPlanByUserId() {
    Optional<DietPlan> dietPlan =
      dietPlannerRepository.findByUserIdAndPlanStatus(
        userSession.getAuthenticatedUserId(),
        PlanStatus.ACTIVE
      );
    if (dietPlan.isEmpty()) {
      throw new EntityNotFoundException("Diet plan not found");
    }
    return dietPlan.get();
  }

  private void processDietPlan(DietPlan dietPlan, DietPlanTrack dietPlanTrack) {
    Macros macros = dietPlan.getMacros();
    dietPlanTrack.setTdee(dietPlan.getTdee());
    dietPlanTrack.setIntake(new Calories(dietPlan.getTargetCalories()));
    dietPlanTrack.setProtein(new Calories(macros.getProtein()));
    dietPlanTrack.setFat(new Calories(macros.getFat()));
    dietPlanTrack.setCarbs(new Calories(macros.getCarbs()));
    dietPlanTrack.setMealKcal(splitForMealKcal(dietPlanTrack));
  }

  public DietPlanTrack createDietPlanTrack(DietPlan dietPlan) {
    LocalDate today = LocalDate.now();
    int dayCount = countDays(dietPlan.getCreatedAt(), today);
    DietPlanTrack dietPlanTrack = new DietPlanTrack(
      dietPlan.getTodayWeight(),
      today,
      dayCount,
      dietPlan.getId()
    );
    processDietPlan(dietPlan, dietPlanTrack);
    return dietPlanTrackRepository.save(dietPlanTrack);
  }

  private int countDays(LocalDate startDate, LocalDate endDate) {
    return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
  }

  public DietPlanTrack findDietPlanTrackById(@NonNull String dietPlanTrackId) {
    Optional<DietPlanTrack> dietPlanTrack = dietPlanTrackRepository.findById(
      dietPlanTrackId
    );
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
    MacrosTrack macros
  ) {
    DietPlanTrack dietPlanTrack = findDietPlanTrackByDietPlanId(dietPlanId);
    MacrosTrack dietPlanMacros = getMacrosByCategory(
      dietPlanTrack.getMealKcal(),
      category
    );
    dietPlanMacros.getProtein().setTaken(macros.getProtein().getTaken());
    dietPlanMacros.getFat().setTaken(macros.getFat().getTaken());
    dietPlanMacros.getCarbs().setTaken(macros.getCarbs().getTaken());
    adjustMacros(dietPlanTrack);
    dietPlanTrack.getIntake().setTaken(calcDeficitTaken(dietPlanTrack));
    return dietPlanTrackRepository.save(dietPlanTrack);
  }

  private MacrosTrack getMacrosByCategory(MealKcal mealKcal, String category) {
    return switch (category.toLowerCase()) {
      case MacrosConstants.BREAKFAST -> mealKcal.getBreakfast();
      case MacrosConstants.LUNCH -> mealKcal.getLunch();
      case MacrosConstants.DINNER -> mealKcal.getDinner();
      case MacrosConstants.SNACK -> mealKcal.getSnack();
      default -> throw new BadRequestException("Invalid category: " + category);
    };
  }

  private double calcDeficitTaken(DietPlanTrack dietPlanTrack) {
    return NutrientsCalculator.calcTakenDeficit(
      new MacrosTrack(
        dietPlanTrack.getProtein(),
        dietPlanTrack.getFat(),
        dietPlanTrack.getCarbs()
      )
    );
  }

  private void adjustMacros(DietPlanTrack dietPlanTrack) {
    MealKcal mealKcal = dietPlanTrack.getMealKcal();
    dietPlanTrack
      .getProtein()
      .setTaken(
        mealKcal.getBreakfast().getProtein().getTaken() +
          mealKcal.getLunch().getProtein().getTaken() +
          mealKcal.getDinner().getProtein().getTaken() +
          mealKcal.getSnack().getProtein().getTaken()
      );
    dietPlanTrack
      .getCarbs()
      .setTaken(
        mealKcal.getBreakfast().getCarbs().getTaken() +
          mealKcal.getLunch().getCarbs().getTaken() +
          mealKcal.getDinner().getCarbs().getTaken() +
          mealKcal.getSnack().getCarbs().getTaken()
      );
    dietPlanTrack
      .getFat()
      .setTaken(
        mealKcal.getBreakfast().getFat().getTaken() +
          mealKcal.getLunch().getFat().getTaken() +
          mealKcal.getDinner().getFat().getTaken() +
          mealKcal.getSnack().getFat().getTaken()
      );
  }

  public MealKcal getMealKcal(String dietPlanId) {
    Optional<DietPlanTrack> dietPlanTrack =
      dietPlanTrackRepository.findFirstByCreatedAtAndDietPlanId(
        LocalDate.now(),
        dietPlanId
      );
    if (dietPlanTrack.isEmpty()) {
      throw new EntityNotFoundException("No macros found.");
    }
    return splitForMealKcal(dietPlanTrack.get());
  }

  private MealKcal splitForMealKcal(DietPlanTrack dietPlanTrack) {
    MacrosTrack macros = new MacrosTrack(
      dietPlanTrack.getProtein(),
      dietPlanTrack.getFat(),
      dietPlanTrack.getCarbs()
    );
    return new MealKcal(
      NutrientsCalculator.macrosPercentage(macros, 30),
      NutrientsCalculator.macrosPercentage(macros, 30),
      NutrientsCalculator.macrosPercentage(macros, 15),
      NutrientsCalculator.macrosPercentage(macros, 25)
    );
  }

  public MetricsDTO getMetricsByDateRange() {
    List<String> days = new ArrayList<>();
    List<Double> weights = new ArrayList<>();
    Date[] dateRange = getOneWeekDateRange();
    DietPlan dietPlan = findDietPlanByUserId();
    DietPlanTrack dietPlanTrack = findDietPlanTrackByDietPlanId(
      dietPlan.getId()
    );
    dietPlanTrackRepository
      .findByDateRangeAndDietPlanId(
        dateRange[0],
        dateRange[1],
        dietPlan.getId()
      )
      .forEach(track -> {
        days.add(getDayName(track.getCreatedAt()));
        weights.add(track.getWeight());
      });
    return buildMetrics(
      dietPlanTrack,
      dietPlan.getTitle(),
      dietPlan.getDuration(),
      days,
      weights
    );
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
    List<Double> weights
  ) {
    Calories intake = dietPlanTrack.getIntake();
    return MetricsDTO.builder()
      .title(title)
      .taken(
        NutrientsCalculator.findPercentage(intake.getTaken(), intake.getTotal())
      )
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
    return dietPlannerRepository
      .findByUserIdAndPlanStatus(
        userSession.getAuthenticatedUserId(),
        PlanStatus.ACTIVE
      )
      .isPresent();
  }

  public long getCaloriesByMealCategory(String category) {
    try {
      DietPlan dietPlan = findDietPlanByUserId();
      DietPlanTrack dietPlanTrack = findDietPlanTrackByDietPlanId(
        dietPlan.getId()
      );
      MacrosTrack macros = getMacrosByCategory(
        dietPlanTrack.getMealKcal(),
        category
      );
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
        dietPlan.setPlanStatus(PlanStatus.INACTIVE);
        dietPlannerRepository.save(dietPlan);
        return true;
      }
      return false;
    } catch (EntityNotFoundException ignored) {
      return true;
    }
  }

  public DietPlanHistoryDTO findPastDietPlan() {
    Optional<DietPlanHistoryDTO> dietPlan =
      dietPlannerRepository.findFirstDietPlanHistoryByUserId(
        userSession.getAuthenticatedUserId()
      );
    if (dietPlan.isEmpty()) {
      throw new EntityNotFoundException("No past plan found.");
    }
    return dietPlan.get();
  }
}
