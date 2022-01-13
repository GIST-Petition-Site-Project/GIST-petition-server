package com.gistpetition.api.exception.post;

import org.springframework.http.HttpStatus;

public class DuplicatedAnswerException extends PostException {
    private static final String MESSAGE = "이미 답변이 존재합니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public DuplicatedAnswerException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
