package org.karthick.macrova.dietplan.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.karthick.macrova.shared.model.Calories;

@Getter
@Setter
public class DietPlanOverviewDTO {

  private String id;
  private double weight;
  private double tdee;
  private Calories intake;
  private Calories protein;
  private Calories carbs;
  private Calories fat;
  private LocalDate createdAt;
}
