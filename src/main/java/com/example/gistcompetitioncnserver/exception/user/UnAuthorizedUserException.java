package com.example.gistcompetitioncnserver.exception.user;

import org.springframework.http.HttpStatus;

public class UnAuthorizedUserException extends UserException{
    private static final String MESSAGE = "권한이 없습니다.";
    private static final HttpStatus HTTP_STATUS = null;

    public UnAuthorizedUserException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
