package com.example.gistcompetitioncnserver.exception.post;

import org.springframework.http.HttpStatus;

public class NeedEmailAuthorization extends PostException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.UNAUTHORIZED;
    private static final String MESSAGE = "이메일 인증이 필요합니다";

    public NeedEmailAuthorization() {
        super(MESSAGE, HTTP_STATUS);
    }
}
