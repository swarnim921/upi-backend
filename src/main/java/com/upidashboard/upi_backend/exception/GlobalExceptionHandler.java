package com.upidashboard.upi_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  // Handle validation errors (@NotNull, @Email, etc.)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String msg = error.getDefaultMessage();
      errors.put(fieldName, msg);
    });

    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  // Handle all other unhandled exceptions
  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGeneralException(Exception ex) {

    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage());

    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}