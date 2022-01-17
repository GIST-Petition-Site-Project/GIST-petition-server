package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class NoSuchPetitionException extends PetitionException {
    private static final String MESSAGE = "존재하지 않는 청원입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public NoSuchPetitionException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
