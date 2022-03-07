package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class AlreadyAnsweredException extends PetitionException {
    private static final String MESSAGE = "이미 답변이 존재합니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public AlreadyAnsweredException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
