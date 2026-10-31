package org.karthick.macrova.dietplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.karthick.macrova.dietplan.enums.Activity;
import org.karthick.macrova.dietplan.enums.Gender;
import org.karthick.macrova.dietplan.enums.Plan;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BodyMetricsRequest {

  private String title;
  private Plan plan;
  private Gender gender;
  private int age;
  private double height;
  private double weight;
  private double targetWeight;
  private Activity activity;
}
