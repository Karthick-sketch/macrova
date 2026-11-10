package org.karthick.macrova.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealKcal {

  private MacrosTrack breakfast;
  private MacrosTrack lunch;
  private MacrosTrack snack;
  private MacrosTrack dinner;
}
