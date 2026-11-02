package org.karthick.macrova.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MacroPreference {

  private double protein;
  private double carbs;
  private double fat;
}
