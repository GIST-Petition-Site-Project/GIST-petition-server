package com.gistpetition.api.exception.user;

import org.springframework.http.HttpStatus;

public class NotConfirmedEmailException extends UserException {
    private static final String MESSAGE = "이메일 인증이 필요합니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public NotConfirmedEmailException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
