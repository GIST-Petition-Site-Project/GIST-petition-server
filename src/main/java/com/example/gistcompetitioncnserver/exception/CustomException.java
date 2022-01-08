package com.example.gistcompetitioncnserver.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException{
    public final HttpStatus httpStatus;
    public final Throwable cause;

    public CustomException(String message, Throwable cause) {
        this(message, cause, HttpStatus.BAD_REQUEST);
    }
    public CustomException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message);
        this.cause = cause;
        this.httpStatus = httpStatus;
    }
}
