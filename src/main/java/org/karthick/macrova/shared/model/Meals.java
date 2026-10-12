package org.karthick.macrova.shared.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Meals {

  private List<String> breakfast;
  public List<String> lunch;
  private List<String> snack;
  private List<String> dinner;
}
