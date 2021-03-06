package com.gistpetition.api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class ControllerAdvice {
    private final static Logger LOGGER = LoggerFactory.getLogger(ControllerAdvice.class);

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handle(ApplicationException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(WrappedException.class)
    public ResponseEntity<ErrorResponse> handle(WrappedException ex) {
        LOGGER.info(String.format("WrappedException: %s", ex.getCause().getMessage()));
        return ResponseEntity.status(ex.getHttpStatus()).body(new ErrorResponse(ex.getCause().getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        LOGGER.info(String.format("MethodArgumentNotValidException: %s", message));
        return ResponseEntity.badRequest().body(new ErrorResponse(message));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handle(NoHandlerFoundException ex) {
        LOGGER.info(String.format("NoHandlerFoundException: %s", ex.getMessage()));
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handle(TypeMismatchException ex) {
        LOGGER.info(String.format("TypeMismatchException: %s", ex.getMessage()));
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handle(HttpRequestMethodNotSupportedException ex) {
        LOGGER.info(String.format("HttpRequestMethodNotSupportedException: %s", ex.getMessage()));
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handle(OptimisticLockingFailureException ex) {
        LOGGER.info(String.format("OptimisticLockingFailureException: %s", ex.getMessage()));
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> validException(Exception ex) {
        LOGGER.error("InternalServerError: ", ex);
        return ResponseEntity.internalServerError().body(new ErrorResponse("Internal Server Error"));
    }
}
