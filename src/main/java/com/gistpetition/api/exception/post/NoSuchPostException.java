package com.gistpetition.api.exception.post;

import org.springframework.http.HttpStatus;

public class NoSuchPostException extends PostException {
    private static final String MESSAGE = "존재하지 않는 청원입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public NoSuchPostException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
