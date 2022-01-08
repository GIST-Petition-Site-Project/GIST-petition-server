package com.example.gistcompetitioncnserver.exception;

import com.example.gistcompetitioncnserver.exception.post.PostException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handle(CustomException ex) {
        return ResponseEntity.status(ex.httpStatus).body(String.format("%s, %s", ex.getMessage(), ex.getCause().getMessage()));
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<String> handle(ApplicationException ex) {
        return ResponseEntity.status(ex.getHttpStatus( )).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> validException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(new ErrorMessage(400, message));
    }
}
