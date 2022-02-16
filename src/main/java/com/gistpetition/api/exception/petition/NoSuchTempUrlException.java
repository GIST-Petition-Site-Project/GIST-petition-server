package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class NoSuchTempUrlException extends PetitionException {
    private static final String MESSAGE = "해당 URL의 청원은 존재하지 않습니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public NoSuchTempUrlException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
