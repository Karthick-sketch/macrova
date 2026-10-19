package org.karthick.macrova.dietplan.entity;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.karthick.macrova.shared.model.Calories;
import org.karthick.macrova.shared.model.MealKcal;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "diet_plan_tracks")
public class DietPlanTrack {

  @Id
  private String id;

  private double weight;
  private double tdee;
  private Calories intake;
  private Calories protein;
  private Calories carbs;
  private Calories fat;
  private MealKcal mealKcal;
  private int dayCount;
  private LocalDate createdAt;
  private String dietPlanId;

  public DietPlanTrack(
    double weight,
    LocalDate createdAt,
    int dayCount,
    String dietPlanId
  ) {
    this.weight = weight;
    this.createdAt = createdAt;
    this.dayCount = dayCount;
    this.dietPlanId = dietPlanId;
  }
}
