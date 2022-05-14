package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class NotValidStatusToRejectPetitionException extends PetitionException {
    private static final String MESSAGE = "반려할 수 없는 상태의 청원입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public NotValidStatusToRejectPetitionException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
