package com.gistpetition.api.exception;

import org.springframework.http.HttpStatus;

public class WrappedException extends ApplicationException {

    private final Throwable cause;

    public WrappedException(String message, Throwable cause) {
        this(message, cause, HttpStatus.BAD_REQUEST);
    }

    public WrappedException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, httpStatus);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

}
