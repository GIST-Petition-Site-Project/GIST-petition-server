package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class NotAnsweredPetitionException extends PetitionException {
    private static final String MESSAGE = "답변이 존재하지 않습니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public NotAnsweredPetitionException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
