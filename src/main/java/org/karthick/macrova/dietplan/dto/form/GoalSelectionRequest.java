package org.karthick.macrova.dietplan.dto.form;

import lombok.Data;
import org.karthick.macrova.dietplan.enums.Goal;

@Data
public class GoalSelectionRequest {

  private Goal goal;
  private int duration;
}
