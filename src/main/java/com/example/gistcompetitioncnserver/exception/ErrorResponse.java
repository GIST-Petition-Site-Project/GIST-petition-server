package com.example.gistcompetitioncnserver.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;


public class ErrorResponse {
    private String message;

    public ErrorResponse() {
    }

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
