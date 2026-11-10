package org.karthick.macrova.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MacrosTrack {

  private Calories protein;
  private Calories fat;
  private Calories carbs;

  public MacrosTrack(double protein, double fat, double carbs) {
    this.protein = new Calories(protein);
    this.fat = new Calories(fat);
    this.carbs = new Calories(carbs);
  }
}
