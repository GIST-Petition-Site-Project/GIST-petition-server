package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class NotValidStatusToAnswerPetitionException extends PetitionException {
    private static final String MESSAGE = "답변을 입력할 수 없는 상태의 청원입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public NotValidStatusToAnswerPetitionException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
