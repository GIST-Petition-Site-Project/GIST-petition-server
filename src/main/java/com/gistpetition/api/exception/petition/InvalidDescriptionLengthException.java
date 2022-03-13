package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class InvalidDescriptionLengthException extends PetitionException{
    private static final String MESSAGE = "내용 길이가 올바르지 않습니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public InvalidDescriptionLengthException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
