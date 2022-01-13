package com.gistpetition.api.exception;

import org.springframework.http.HttpStatus;

public class ApplicationException extends RuntimeException {
    private final HttpStatus httpStatus;

    public ApplicationException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
