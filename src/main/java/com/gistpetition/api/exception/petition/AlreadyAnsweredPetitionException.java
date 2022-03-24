package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class AlreadyAnsweredPetitionException extends PetitionException {
    private static final String MESSAGE = "이미 답변된 청원입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public AlreadyAnsweredPetitionException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
