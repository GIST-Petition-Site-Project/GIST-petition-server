package com.gistpetition.api.exception.user;

import com.gistpetition.api.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class UserException extends ApplicationException {
    public UserException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
