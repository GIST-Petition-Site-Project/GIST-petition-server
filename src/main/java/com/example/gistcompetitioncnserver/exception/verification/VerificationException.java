package com.example.gistcompetitioncnserver.exception.verification;

import com.example.gistcompetitioncnserver.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class VerificationException extends ApplicationException {
    public VerificationException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
