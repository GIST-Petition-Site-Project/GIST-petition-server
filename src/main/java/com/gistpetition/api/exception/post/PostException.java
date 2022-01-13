package com.gistpetition.api.exception.post;

import com.gistpetition.api.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class PostException extends ApplicationException {
    public PostException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
