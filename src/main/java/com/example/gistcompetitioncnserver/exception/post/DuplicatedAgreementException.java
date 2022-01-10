package com.example.gistcompetitioncnserver.exception.post;

import org.springframework.http.HttpStatus;

public class DuplicatedAgreementException extends PostException {
    private static final String MESSAGE = "이미 동의하셨습니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public DuplicatedAgreementException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
