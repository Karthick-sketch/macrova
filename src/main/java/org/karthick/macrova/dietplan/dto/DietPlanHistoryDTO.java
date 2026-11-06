package org.karthick.macrova.dietplan.dto;

import lombok.Data;
import org.karthick.macrova.dietplan.enums.Plan;
import org.karthick.macrova.dietplan.enums.PlanStatus;

@Data
public class DietPlanHistoryDTO {

  private String id;
  private String title;
  private Plan plan;
  private double weight;
  private double targetWeight;
  private int duration;
  private PlanStatus planStatus;
}
