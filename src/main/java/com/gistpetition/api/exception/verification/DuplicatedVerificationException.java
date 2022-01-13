package com.gistpetition.api.exception.verification;

import org.springframework.http.HttpStatus;

public class DuplicatedVerificationException extends VerificationException {
    private static final String MESSAGE = "이미 인증된 정보입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public DuplicatedVerificationException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
