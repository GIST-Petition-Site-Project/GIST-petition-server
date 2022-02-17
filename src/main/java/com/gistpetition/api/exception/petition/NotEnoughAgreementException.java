package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class NotEnoughAgreementException extends PetitionException {
    private static final String MESSAGE = "동의 수가 충분하지 않습니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public NotEnoughAgreementException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
