package com.example.gistcompetitioncnserver.exception.post;

import com.example.gistcompetitioncnserver.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class PostException extends ApplicationException {
    public PostException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
