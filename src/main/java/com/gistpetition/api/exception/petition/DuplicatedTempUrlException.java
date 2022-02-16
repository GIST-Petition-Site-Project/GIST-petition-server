package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class DuplicatedTempUrlException extends PetitionException {
    private static final String MESSAGE = "이미 청원에 링크가 존재합니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public DuplicatedTempUrlException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
