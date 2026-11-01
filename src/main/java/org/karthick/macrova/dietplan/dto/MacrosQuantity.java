package org.karthick.macrova.dietplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MacrosQuantity {

  private double minimum;
  private double maximum;
}
