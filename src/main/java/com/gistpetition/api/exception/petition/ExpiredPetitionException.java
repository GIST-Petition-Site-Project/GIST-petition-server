package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class ExpiredPetitionException extends PetitionException {
    private static final String MESSAGE = "만료된 청원입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public ExpiredPetitionException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
