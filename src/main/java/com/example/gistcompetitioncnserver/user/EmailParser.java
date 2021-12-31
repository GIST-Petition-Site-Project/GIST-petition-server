package com.example.gistcompetitioncnserver.user;

import java.util.Arrays;

public class EmailParser {
    public static String parseDomainFrom(String email) {
        Long numOfAt = email.chars().filter(ch -> ch == '@').count();
        if (!numOfAt.equals(1L)) {
            return null;
        }
        return Arrays.asList(email.split("@")).get(1);
    }
}
