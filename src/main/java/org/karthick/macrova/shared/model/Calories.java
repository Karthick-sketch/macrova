package org.karthick.macrova.shared.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Calories {

  private Double taken;
  private Double total;

  public Calories(Double total) {
    this.total = total;
  }
}
