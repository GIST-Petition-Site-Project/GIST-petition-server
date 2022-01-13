package com.gistpetition.api.exception.comment;

import com.gistpetition.api.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class CommentException extends ApplicationException {
    public CommentException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

}
