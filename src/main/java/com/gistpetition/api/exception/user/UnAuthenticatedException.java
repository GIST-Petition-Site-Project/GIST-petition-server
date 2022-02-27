package com.gistpetition.api.exception.user;

import org.springframework.http.HttpStatus;

public class UnAuthenticatedException extends UserException {
    private static final String MESSAGE = "인증되지 않은 사용자입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.UNAUTHORIZED;

    public UnAuthenticatedException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
