package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class NotRejectedPetitionException extends PetitionException {
    private static final String MESSAGE = "반려되지 않은 청원입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public NotRejectedPetitionException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
