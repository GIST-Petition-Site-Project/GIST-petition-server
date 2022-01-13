package com.gistpetition.api.exception.verification;

import org.springframework.http.HttpStatus;

public class NotConfirmedVerificationCodeException extends VerificationException {
    private static final String MESSAGE = "인증되지 않은 코드입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public NotConfirmedVerificationCodeException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
