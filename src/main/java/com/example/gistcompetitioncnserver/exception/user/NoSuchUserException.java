package com.example.gistcompetitioncnserver.exception.user;

import org.springframework.http.HttpStatus;

public class NoSuchUserException extends UserException {
    private static final String MESSAGE = "존재하지 않는 회원입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public NoSuchUserException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
