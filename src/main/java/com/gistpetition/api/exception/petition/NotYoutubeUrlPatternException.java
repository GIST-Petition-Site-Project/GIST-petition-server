package com.gistpetition.api.exception.petition;

import org.springframework.http.HttpStatus;

public class NotYoutubeUrlPatternException extends PetitionException {
    private static final String MESSAGE = "잘못된 형태의 유튜브 URL 입니다.";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public NotYoutubeUrlPatternException() {
        super(MESSAGE, HTTP_STATUS);
    }
}
