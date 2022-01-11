package com.example.gistcompetitioncnserver.exception.user;

import com.example.gistcompetitioncnserver.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class UserException extends ApplicationException {
    public UserException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
