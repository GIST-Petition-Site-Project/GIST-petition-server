package com.example.gistcompetitioncnserver.exception.user;

import org.springframework.http.HttpStatus;

public class DuplicatedUserException extends UserException {
    private static final String MESSAGE = "이미 존재하는 회원입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public DuplicatedUserException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
