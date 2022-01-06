package com.example.gistcompetitioncnserver.exception.post;

import org.springframework.http.HttpStatus;

public class NonExistentUser extends PostException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;
    private static final String MESSAGE = "존재하지 않는 계정입니다";

    public NonExistentUser() {
        super(MESSAGE, HTTP_STATUS);
    }
}
