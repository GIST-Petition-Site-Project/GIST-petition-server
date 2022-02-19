package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class NotReleasedPetitionException extends PetitionException {
    private static final String MESSAGE = "공개되지 않은 청원 게시물입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public NotReleasedPetitionException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
