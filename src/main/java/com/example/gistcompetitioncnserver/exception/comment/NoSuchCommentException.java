package com.example.gistcompetitioncnserver.exception.comment;

import org.springframework.http.HttpStatus;

public class NoSuchCommentException extends CommentException {
    private static final String MESSAGE = "존재하지 않는 댓글입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public NoSuchCommentException() {
        super(MESSAGE, HTTP_STATUS);
    }
}