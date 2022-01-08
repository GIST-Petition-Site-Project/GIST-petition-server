package com.example.gistcompetitioncnserver.exception.user;

import org.springframework.http.HttpStatus;

public class InvalidEmailFormException extends UserException{
    private static final String MESSAGE = "이메일 형식이 올바르지 않습니다.";
    private static final HttpStatus HTTP_STATUS = null;

    public InvalidEmailFormException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
