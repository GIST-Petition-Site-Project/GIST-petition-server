package com.example.gistcompetitioncnserver.exception.post;

import org.springframework.http.HttpStatus;

public class NonExistentPost extends PostException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;
    private static final String MESSAGE = "존재하지 않는 게시글입니다";

    public NonExistentPost() {
        super(MESSAGE, HTTP_STATUS);
    }
}

