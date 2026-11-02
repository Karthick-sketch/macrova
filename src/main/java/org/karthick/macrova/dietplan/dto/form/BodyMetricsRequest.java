package org.karthick.macrova.dietplan.dto.form;

import lombok.Data;
import org.karthick.macrova.dietplan.enums.Activity;
import org.karthick.macrova.dietplan.enums.Gender;
import org.karthick.macrova.dietplan.enums.Plan;

@Data
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
