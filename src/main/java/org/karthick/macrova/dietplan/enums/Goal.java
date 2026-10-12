package org.karthick.macrova.dietplan.enums;

import lombok.Getter;

@Getter
public enum Goal {
  MILD(15),
  MODERATE(20),
  AGGRESSIVE(25);

  private final int value;

  Goal(int value) {
    this.value = value;
  }
}
