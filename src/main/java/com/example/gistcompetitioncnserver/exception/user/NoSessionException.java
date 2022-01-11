package com.example.gistcompetitioncnserver.exception.user;

import org.springframework.http.HttpStatus;

public class NoSessionException extends UserException {
    private static final String MESSAGE = "세션유저가 존재하지 않습니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.UNAUTHORIZED;

    public NoSessionException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
