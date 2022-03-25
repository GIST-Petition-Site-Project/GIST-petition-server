package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class AlreadyRejectedPetitionException extends PetitionException {
    private static final String MESSAGE = "이미 반려된 청원입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public AlreadyRejectedPetitionException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
