package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class InvalidTitleLengthException extends PetitionException{
    private static final String MESSAGE = "제목 길이가 올바르지 않습니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public InvalidTitleLengthException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
