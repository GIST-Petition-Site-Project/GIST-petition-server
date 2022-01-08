package com.example.gistcompetitioncnserver.exception.user;

import org.springframework.http.HttpStatus;

public class UnAuthenticatedUserException extends UserException{
    private static final String MESSAGE = "이메일 인증이 필요합니다.";
    private static final HttpStatus HTTP_STATUS = null;

    public UnAuthenticatedUserException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
