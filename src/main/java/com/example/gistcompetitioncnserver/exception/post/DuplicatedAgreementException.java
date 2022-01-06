package com.example.gistcompetitioncnserver.exception.post;

import org.springframework.http.HttpStatus;

public class DuplicatedAgreementException extends PostException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.METHOD_NOT_ALLOWED;
    private static final String MESSAGE = "동의는 한 번만 할 수 있습니다";
    public DuplicatedAgreementException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
