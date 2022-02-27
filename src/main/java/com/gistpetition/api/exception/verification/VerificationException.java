package com.gistpetition.api.exception.verification;

import com.gistpetition.api.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class VerificationException extends ApplicationException {
    public VerificationException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
