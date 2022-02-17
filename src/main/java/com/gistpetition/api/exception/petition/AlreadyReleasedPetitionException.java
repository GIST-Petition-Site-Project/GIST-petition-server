package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class AlreadyReleasedPetitionException extends PetitionException {
    private static final String MESSAGE = "이미 공개된 청원입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public AlreadyReleasedPetitionException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
