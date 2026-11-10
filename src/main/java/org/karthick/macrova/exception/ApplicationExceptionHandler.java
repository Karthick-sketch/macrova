package org.karthick.macrova.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
    EntityNotFoundException e
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      new ErrorResponse(e.getMessage())
    );
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleBadRequestException(
    BadRequestException e
  ) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      new ErrorResponse(e.getMessage())
    );
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleBadGatewayException(
    BadGatewayException e
  ) {
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
      new ErrorResponse(e.getMessage())
    );
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleUnauthorizedException(
    UnauthorizedException e
  ) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
      new ErrorResponse(e.getMessage())
    );
  }
}
