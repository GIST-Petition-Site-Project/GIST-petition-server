package com.example.gistcompetitioncnserver.exception.user;

import org.springframework.http.HttpStatus;

public class NotMatchedPasswordException extends UserException {
    private static final String MESSAGE = "비밀번호가 맞지 않습니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public NotMatchedPasswordException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
