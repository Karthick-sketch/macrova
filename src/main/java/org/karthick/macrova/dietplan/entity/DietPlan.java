package org.karthick.macrova.dietplan.entity;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.karthick.macrova.dietplan.enums.*;
import org.karthick.macrova.shared.model.Macros;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "diet_plans")
public class DietPlan {

  @Id
  private String id;

  private String title;
  private Plan plan;
  private int age;
  private Gender gender;
  private double height;
  private double weight;
  private double todayWeight;
  private Goal goal;
  private double targetWeight;
  private int duration;
  private Activity activity;
  private double tdee;
  private double targetCalories;
  private double currentCalories;
  private DietType dietType;
  private MealType mealType;
  private Macros macros;
  private int mealCount;
  private boolean eveningSnack;
  private LocalDate createdAt;
  private String userId;

  @Builder.Default
  private PlanStatus planStatus = PlanStatus.PENDING;

  @Builder.Default
  private FormStatus formStatus = FormStatus.BODY_METRICS;
}
