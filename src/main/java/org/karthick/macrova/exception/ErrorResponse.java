package org.karthick.macrova.exception;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

  private List<String> error;

  public ErrorResponse(String errorMessage) {
    this.error = List.of(errorMessage);
  }
}
