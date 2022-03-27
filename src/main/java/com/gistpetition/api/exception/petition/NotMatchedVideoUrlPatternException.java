package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class NotMatchedVideoUrlPatternException extends PetitionException {
    private static final String MESSAGE = "잘못된 형태의 Video URL 입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public NotMatchedVideoUrlPatternException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
