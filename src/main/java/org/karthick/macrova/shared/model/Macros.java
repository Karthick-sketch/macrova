package org.karthick.macrova.shared.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Macros {

  private Calories protein;
  private Calories fat;
  private Calories carbs;

  public Macros(double protein, double fat, double carbs) {
    this.protein = new Calories(protein);
    this.fat = new Calories(fat);
    this.carbs = new Calories(carbs);
  }
}
