package org.karthick.macrova.exception;

public class BadGatewayException extends RuntimeException {

  private final AIFoodSuggestErrorResponse errorResponse;

  public BadGatewayException(AIFoodSuggestErrorResponse errorResponse) {
    super(errorResponse.getErrorMessage());
    this.errorResponse = errorResponse;
  }

  public AIFoodSuggestErrorResponse getErrorResponse() {
    return errorResponse;
  }
}
