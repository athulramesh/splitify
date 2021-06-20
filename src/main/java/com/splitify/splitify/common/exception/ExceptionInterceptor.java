package com.splitify.splitify.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** The exception interceptor. */
@ControllerAdvice
public class ExceptionInterceptor extends ResponseEntityExceptionHandler {
  @ExceptionHandler(SplitifyException.class)
  public final ResponseEntity<Object> handleAllExceptions(SplitifyException ex) {
    SplitifyExceptionSchema exceptionResponse = new SplitifyExceptionSchema(ex.getMessage());
    return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
  }
}
