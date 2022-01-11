package com.example.gistcompetitioncnserver.exception.comment;

import com.example.gistcompetitioncnserver.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class CommentException extends ApplicationException {
    public CommentException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

}
