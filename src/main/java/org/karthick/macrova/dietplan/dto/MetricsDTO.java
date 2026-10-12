package org.karthick.macrova.dietplan.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetricsDTO {

  private String title;
  private double taken;
  private int duration;
  private int currentDay;
  private double currentWeight;
  private double intake;
  private double protein;
  private double carbs;
  private double fat;
  private List<String> days;
  private List<Double> weights;
}
