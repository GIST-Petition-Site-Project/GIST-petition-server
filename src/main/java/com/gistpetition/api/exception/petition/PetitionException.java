package com.gistpetition.api.exception.petition;

import com.gistpetition.api.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class PetitionException extends ApplicationException {
    public PetitionException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
