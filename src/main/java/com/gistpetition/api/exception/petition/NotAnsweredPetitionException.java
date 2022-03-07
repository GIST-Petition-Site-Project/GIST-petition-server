package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class NotAnsweredPetitionException extends PetitionException {
    private static final String MESSAGE = "답변되지 않은 청원입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public NotAnsweredPetitionException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
