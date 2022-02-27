package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class NoSuchCategoryException extends PetitionException {
    private static final String MESSAGE = "존재하지 않는 카테고리입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public NoSuchCategoryException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
