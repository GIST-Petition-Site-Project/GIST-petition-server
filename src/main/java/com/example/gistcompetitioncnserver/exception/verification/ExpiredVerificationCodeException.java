package com.example.gistcompetitioncnserver.exception.verification;

import org.springframework.http.HttpStatus;

public class ExpiredVerificationCodeException extends VerificationException {
    private static final String MESSAGE = "만료된 인증 코드입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public ExpiredVerificationCodeException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
