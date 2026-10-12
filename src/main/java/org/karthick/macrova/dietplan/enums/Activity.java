package org.karthick.macrova.dietplan.enums;

import lombok.Getter;

@Getter
public enum Activity {
  SEDENTARY(1.2),
  LIGHT(1.375),
  MODERATE(1.55),
  HARD(1.725),
  ATHLETE(1.9);

  private final double value;

  Activity(double value) {
    this.value = value;
  }
}
