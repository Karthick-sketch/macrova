package org.karthick.macrova.util;

import org.karthick.macrova.dietplan.enums.Activity;
import org.karthick.macrova.dietplan.enums.Gender;
import org.karthick.macrova.dietplan.enums.Plan;
import org.karthick.macrova.shared.model.Macros;

/**
 * Utility class providing static methods for calculating key nutritional and
 * metabolic values used in diet planning.
 *
 * <p>All calculations are based on standard nutritional science formulas,
 * including the Mifflin-St Jeor equation for BMR and standard macro
 * calorie conversion ratios (protein: 4 kcal/g, fat: 9 kcal/g, carbs: 4 kcal/g).
 *
 * <p>This class is non-instantiable; all methods are static.
 */
public final class NutrientsCalculator {

  /**
   * Calculates the Basal Metabolic Rate (BMR) using the Mifflin-St Jeor equation.
   *
   * <p>BMR represents the number of calories the body needs to maintain basic
   * physiological functions (breathing, circulation, cell production) at complete rest.
   *
   * <p>Formula: {@code BMR = (10 × weight) + (6.25 × height) − (5 × age) + S}
   * where S = +5 for males and −161 for females.
   *
   * @param age    the person's age in years
   * @param weight the person's weight in kilograms
   * @param height the person's height in centimeters
   * @param gender the person's gender ({@link Gender#MALE} or {@link Gender#FEMALE})
   * @return the calculated BMR value in kilocalories per day
   */
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

  /**
   * Calculates the Body Mass Index (BMI).
   *
   * <p>BMI is a measure of body fat based on height and weight. It is used to
   * screen for weight categories that may lead to health problems.
   *
   * <p>Formula: {@code BMI = (weight / height²) × 10,000}
   * where height is in centimeters and weight is in kilograms. The factor of
   * 10,000 converts the result to the standard kg/m² unit.
   *
   * @param height the person's height in centimeters
   * @param weight the person's weight in kilograms
   * @return the calculated BMI value in kg/m²
   */
  public static double findBMI(double height, double weight) {
    return (weight / (height * height)) * 10_000;
  }

  /**
   * Calculates the Total Daily Energy Expenditure (TDEE).
   *
   * <p>TDEE represents the total number of calories burned in a day, accounting
   * for both the basal metabolic rate and the person's physical activity level.
   * It is computed by multiplying the BMR by an activity multiplier.
   *
   * @param age      the person's age in years
   * @param weight   the person's weight in kilograms
   * @param height   the person's height in centimeters
   * @param gender   the person's gender ({@link Gender#MALE} or {@link Gender#FEMALE})
   * @param activity the person's activity level, which provides the multiplier
   *                 (e.g., sedentary, lightly active, very active)
   * @return the calculated TDEE value in kilocalories per day
   * @see #findBMR(int, double, double, Gender)
   */
  public static double findTDEE(
    int age,
    double weight,
    double height,
    Gender gender,
    Activity activity
  ) {
    return findBMR(age, weight, height, gender) * activity.getValue();
  }

  /**
   * Estimates the minimum number of days required to reach a target weight.
   *
   * <p>The minimum duration assumes the fastest safe rate of weight change:
   * <ul>
   *   <li>For {@link Plan#WEIGHT_LOSS}: 1 kg per week (7 days/kg)</li>
   *   <li>For other plans (e.g., muscle gain): 0.5 kg per week (14 days/kg)</li>
   * </ul>
   *
   * @param mass the absolute difference in kilograms between the current weight
   *             and the target weight
   * @param plan the diet plan type (e.g., {@link Plan#WEIGHT_LOSS})
   * @return the minimum estimated duration in days, rounded up to the nearest whole day
   */
  public static int findMinimumDuration(double mass, Plan plan) {
    return (int) Math.ceil(
      Plan.WEIGHT_LOSS.equals(plan) ? mass * 7 : (mass / 0.5) * 7
    );
  }

  /**
   * Estimates the maximum number of days required to reach a target weight.
   *
   * <p>The maximum duration assumes the slowest safe rate of weight change:
   * 0.25 kg per week (28 days/kg).
   *
   * @param mass the absolute difference in kilograms between the current weight
   *             and the target weight
   * @return the maximum estimated duration in days, rounded up to the nearest whole day
   */
  public static int findMaximumDuration(double mass) {
    return (int) Math.ceil((mass / 0.25) * 7);
  }

  /**
   * Calculates a percentage of a given value.
   *
   * <p>For example, {@code percentageOf(2000, 30)} returns {@code 600},
   * representing 30% of 2000 calories.
   *
   * @param value   the base value to calculate the percentage of
   * @param percent the percentage to apply (e.g., 30 for 30%)
   * @return the calculated percentage, rounded to the nearest long
   */
  public static long percentageOf(double value, int percent) {
    return Math.round((value * percent) / 100);
  }

  /**
   * Scales a {@link Macros} object by a given percentage.
   *
   * <p>This is useful for calculating the macros for a specific portion of
   * the total daily intake (e.g., a single meal in a multi-meal plan).
   * Each macro's total value is scaled independently.
   *
   * @param macros  the total daily {@link Macros} to scale
   * @param percent the percentage to apply to each macro's total value
   * @return a new {@link Macros} instance with each macro scaled by the
   *         given percentage
   * @see #percentageOf(double, int)
   */
  public static Macros macrosPercentage(Macros macros, int percent) {
    return new Macros(
      percentageOf(macros.getProtein().getTotal(), percent),
      percentageOf(macros.getFat().getTotal(), percent),
      percentageOf(macros.getCarbs().getTotal(), percent)
    );
  }

  /**
   * Calculates what percentage {@code value} represents of {@code maxValue}.
   *
   * <p>Typically used to determine how much of a daily macro target has been
   * consumed (e.g., tracking progress against a goal).
   *
   * @param value    the partial or current value
   * @param maxValue the total or maximum reference value
   * @return the percentage of {@code value} relative to {@code maxValue},
   *         rounded to the nearest long
   */
  public static long findPercentage(double value, double maxValue) {
    return Math.round((value / maxValue) * 100);
  }

  /**
   * Rounds a kilocalorie value to the nearest 50 or 100.
   *
   * <p>This method is used to produce clean, user-friendly calorie targets.
   * The rounding logic is:
   * <ul>
   *   <li>If the remainder after rounding down to the nearest 100 is &gt; 50,
   *       round up to the next 100.</li>
   *   <li>If the remainder is &gt; 0 (but ≤ 50), round up to the next 50.</li>
   *   <li>If the remainder is 0, no adjustment is made.</li>
   * </ul>
   *
   * @param kcal the raw kilocalorie value to round
   * @return the kilocalorie value rounded to the nearest 50 or 100
   */
  public static long roundCalories(long kcal) {
    long round = (kcal / 100) * 100;
    long difference = kcal - round;
    return round + (difference > 50 ? 100 : (difference > 0 ? 50 : 0));
  }

  /**
   * Calculates a calorie surplus target based on the TDEE.
   *
   * <p>A calorie surplus is used in muscle-gain (bulking) plans. The surplus
   * is computed by adding a percentage of the TDEE to the maintenance level,
   * with an offset of 10% subtracted from the specified percentage to moderate
   * the surplus. The result is then rounded to a clean value.
   *
   * @param tdee    the Total Daily Energy Expenditure in kilocalories
   * @param percent the gross surplus percentage; the effective surplus applied
   *                is {@code (percent - 10)%} of TDEE
   * @return the daily calorie surplus target in kilocalories, rounded to the
   *         nearest 50 or 100
   * @see #roundCalories(long)
   */
  public static long findSurplus(double tdee, int percent) {
    return roundCalories(Math.round(tdee) + percentageOf(tdee, percent - 10));
  }

  /**
   * Calculates a calorie deficit target based on the TDEE.
   *
   * <p>A calorie deficit is used in weight-loss plans. The deficit is computed
   * by subtracting the specified percentage of the TDEE from the maintenance
   * level. The result is then rounded to a clean value.
   *
   * @param tdee    the Total Daily Energy Expenditure in kilocalories
   * @param percent the deficit percentage to subtract from TDEE
   *                (e.g., 20 to subtract 20% of TDEE)
   * @return the daily calorie deficit target in kilocalories, rounded to the
   *         nearest 50 or 100
   * @see #roundCalories(long)
   */
  public static long findDeficit(double tdee, int percent) {
    return roundCalories(Math.round(tdee - percentageOf(tdee, percent)));
  }

  /**
   * Calculates the total calories consumed based on the "taken" macros.
   *
   * <p>Uses standard calorie-per-gram conversion factors:
   * <ul>
   *   <li>Protein: 4 kcal/g</li>
   *   <li>Fat: 9 kcal/g</li>
   *   <li>Carbohydrates: 4 kcal/g</li>
   * </ul>
   *
   * @param macros the {@link Macros} object containing the amounts already
   *               consumed (taken values)
   * @return the total calories consumed as a {@code double}
   */
  public static double calcTakenDeficit(Macros macros) {
    return (
      macros.getProtein().getTaken() * 4 +
      macros.getFat().getTaken() * 9 +
      macros.getCarbs().getTaken() * 4
    );
  }

  /**
   * Calculates the total target calories based on the total (goal) macros.
   *
   * <p>Uses standard calorie-per-gram conversion factors:
   * <ul>
   *   <li>Protein: 4 kcal/g</li>
   *   <li>Fat: 9 kcal/g</li>
   *   <li>Carbohydrates: 4 kcal/g</li>
   * </ul>
   *
   * @param macros the {@link Macros} object containing the total (goal) values
   *               for each macro
   * @return the total target calories, rounded to the nearest long
   */
  public static long calcTotalDeficit(Macros macros) {
    return Math.round(
      macros.getProtein().getTotal() * 4 +
        macros.getFat().getTotal() * 9 +
        macros.getCarbs().getTotal() * 4
    );
  }

  /**
   * Calculates the daily protein target in grams from a given calorie budget.
   *
   * <p>Protein is allocated 30% of the total calorie budget. Since protein
   * provides 4 kcal/g, the gram target is derived by dividing the calorie
   * allocation by 4.
   *
   * @param deficit the total daily calorie target (e.g., a deficit or surplus value)
   *                in kilocalories
   * @return the protein target in grams, rounded to the nearest whole gram
   */
  public static double findProtein(double deficit) {
    return Double.valueOf(Math.round(percentageOf(deficit, 30) / 4.0));
  }

  /**
   * Calculates the daily fat target in grams from a given calorie budget.
   *
   * <p>Fat is allocated 30% of the total calorie budget. Since fat provides
   * 9 kcal/g, the gram target is derived by dividing the calorie allocation by 9.
   *
   * @param deficit the total daily calorie target (e.g., a deficit or surplus value)
   *                in kilocalories
   * @return the fat target in grams, rounded to the nearest whole gram
   */
  public static double findFat(double deficit) {
    return Double.valueOf(Math.round(percentageOf(deficit, 30) / 9.0));
  }

  /**
   * Calculates the daily carbohydrate target in grams from a given calorie budget.
   *
   * <p>Carbohydrates are allocated 40% of the total calorie budget. Since carbs
   * provide 4 kcal/g, the gram target is derived by dividing the calorie
   * allocation by 4.
   *
   * @param deficit the total daily calorie target (e.g., a deficit or surplus value)
   *                in kilocalories
   * @return the carbohydrate target in grams, rounded to the nearest whole gram
   */
  public static double findCarbs(double deficit) {
    return Double.valueOf(Math.round(percentageOf(deficit, 40) / 4.0));
  }
}
