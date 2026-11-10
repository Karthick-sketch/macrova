package org.karthick.macrova.shared.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MacrosPreference {

  private Macros balanced;
  private Macros highProtein;
  private Macros lowCarb;
  private Macros lowFat;
}
