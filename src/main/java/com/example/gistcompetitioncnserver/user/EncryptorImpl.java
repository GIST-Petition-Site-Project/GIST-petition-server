package com.example.gistcompetitioncnserver.user;

import org.springframework.stereotype.Component;

@Component
public class EncryptorImpl implements Encryptor {
    @Override
    public String encode(String raw) {
        return raw + raw;
    }
}
