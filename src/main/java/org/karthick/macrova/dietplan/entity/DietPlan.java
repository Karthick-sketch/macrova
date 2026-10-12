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
  private Integer age;
  private Gender gender;
  private Double height;
  private Double weight;
  private Double todayWeight;
  private Goal goal;
  private Double targetWeight;
  private Integer duration;
  private Activity activity;
  private String userId;
  private Boolean active;
  private LocalDate createdAt;

  public DietPlan() {
    this.createdAt = LocalDate.now();
  }
}
