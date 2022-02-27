package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class DuplicatedAgreementException extends PetitionException {
    private static final String MESSAGE = "이미 동의하셨습니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public DuplicatedAgreementException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
