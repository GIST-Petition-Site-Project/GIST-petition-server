package com.example.gistcompetitioncnserver.exception.post;

import org.springframework.http.HttpStatus;

public class PostException extends RuntimeException {

    private final HttpStatus httpStatus;

    public PostException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
