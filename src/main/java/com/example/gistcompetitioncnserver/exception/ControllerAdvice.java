package com.example.gistcompetitioncnserver.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handle(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorMessage(400, ex.getMessage()));
    }
}
