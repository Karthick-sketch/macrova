package org.karthick.macrova.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIFoodSuggestErrorResponse {

  private String aiResponseId;
  private String errorMessage;
}
