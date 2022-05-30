package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class NotValidStatusToReleasePetitionException extends PetitionException {
    private static final String MESSAGE = "승인할 수 없는 상태의 청원입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public NotValidStatusToReleasePetitionException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
