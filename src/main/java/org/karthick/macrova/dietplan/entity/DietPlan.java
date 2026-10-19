package org.karthick.macrova.dietplan.entity;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.karthick.macrova.dietplan.enums.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
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
  private String userId;
  private boolean active;
  private LocalDate createdAt;

  public DietPlan() {
    this.createdAt = LocalDate.now();
  }
}
