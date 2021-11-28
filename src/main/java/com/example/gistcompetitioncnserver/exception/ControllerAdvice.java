package com.example.gistcompetitioncnserver.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorMessage> handle(CustomException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorMessage(400, ex.getMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Void> userNotFoundExceptionHandle() {
        return ResponseEntity.notFound().build();
    }
}
