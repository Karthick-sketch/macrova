package org.karthick.macrova.util;

import org.karthick.macrova.dietplan.enums.Activity;
import org.karthick.macrova.dietplan.enums.Gender;
import org.karthick.macrova.dietplan.enums.Plan;
import org.karthick.macrova.shared.model.Macros;

public final class CaloriesCalculator {

  /** BMR - Basal Metabolic Rate */
  public static double findBMR(
    int age,
    double weight,
    double height,
    Gender gender
  ) {
    return (
      10 * weight + 6.25 * height - 5 * age + (gender == Gender.MALE ? 5 : -161)
    );
  }

  /** BMI - Body Mass Index */
  public static double findBMI(double height, double weight) {
    return (weight / (height * height)) * 10_000;
  }

  /** TDEE - Total Daily Energy Expenditure */
  public static double findTDEE(
    int age,
    double weight,
    double height,
    Gender gender,
    Activity activity
  ) {
    return findBMR(age, weight, height, gender) * activity.getValue();
  }

  public static int findMinimumDuration(double mass, Plan plan) {
    return (int) Math.ceil(
      Plan.WEIGHT_LOSS.equals(plan) ? mass * 7 : (mass / 0.5) * 7
    );
  }

  public static int findMaximumDuration(double mass) {
    return (int) Math.ceil((mass / 0.25) * 7);
  }

  public static long kcalPercentage(double kcal, int percent) {
    return Math.round(kcal - percentageOf(kcal, percent));
  }

  public static long percentageOf(double value, int percent) {
    return Math.round((value * percent) / 100);
  }

  public static Macros macrosPercentage(Macros macros, int percent) {
    return new Macros(
      percentageOf(macros.getProtein().getTotal(), percent),
      percentageOf(macros.getFat().getTotal(), percent),
      percentageOf(macros.getCarbs().getTotal(), percent)
    );
  }

  public static long calcPercentage(double value, double maxValue) {
    return Math.round((value / maxValue) * 100);
  }

  public static long roundKcal(long kcal) {
    long round = (kcal / 100) * 100;
    long difference = kcal - round;
    return round + (difference > 50 ? 100 : (difference > 0 ? 50 : 0));
  }

  public static long calcSurplus(double kcal, int percent) {
    return roundKcal(Math.round(kcal) + percentageOf(kcal, percent - 10));
  }

  public static long calcDeficit(double kcal, int percent) {
    return roundKcal(kcalPercentage(kcal, percent));
  }

  public static double calcTakenDeficit(Macros macros) {
    return (
      macros.getProtein().getTaken() * 4 +
      macros.getFat().getTaken() * 9 +
      macros.getCarbs().getTaken() * 4
    );
  }

  public static long calcTotalDeficit(Macros macros) {
    return Math.round(
      macros.getProtein().getTotal() * 4 +
        macros.getFat().getTotal() * 9 +
        macros.getCarbs().getTotal() * 4
    );
  }

  public static Double calcProtein(double deficit) {
    return Double.valueOf(Math.round(percentageOf(deficit, 30) / 4.0));
  }

  public static Double calcFat(double deficit) {
    return Double.valueOf(Math.round(percentageOf(deficit, 30) / 9.0));
  }

  public static Double calcCarbs(double deficit) {
    return Double.valueOf(Math.round(percentageOf(deficit, 40) / 4.0));
  }
}
