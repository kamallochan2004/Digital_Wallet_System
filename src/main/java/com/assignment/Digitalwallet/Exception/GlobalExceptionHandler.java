package com.assignment.Digitalwallet.Exception;

import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
    public class GlobalExceptionHandler {
        @ExceptionHandler({UserAlreadyExistsException.class, InsufficientFundsException.class,InvalidCurrencyException.class, UserNotFoundException.class, IllegalArgumentException.class, ProductNotFoundException.class})
        public ResponseEntity<Map<String, String>> handleBusinessExceptions(RuntimeException ex) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if (ex instanceof UserAlreadyExistsException) {
                status = HttpStatus.CONFLICT; 
            } else if (ex instanceof UserNotFoundException || ex instanceof ProductNotFoundException) {
                status = HttpStatus.NOT_FOUND;
            }
            return new ResponseEntity<>(Collections.singletonMap("error", ex.getMessage()), status);
        }
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}