package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.exception.WrappedException;

import java.util.Locale;

public enum UserRole {
    ADMIN,
    MANAGER,
    USER;

    public static UserRole ignoringCaseValueOf(String name) {
        try {
            return valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new WrappedException("존재하지 않는 유저 권한입니다.", e);
        }
    }
}
