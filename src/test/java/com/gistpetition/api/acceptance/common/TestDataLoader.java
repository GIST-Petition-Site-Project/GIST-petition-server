package com.gistpetition.api.acceptance.common;

import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import com.gistpetition.api.utils.password.Encoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.gistpetition.api.acceptance.common.TUser.T_ADMIN;

@Profile("!dev && !prod")
@Component
public class TestDataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final Encoder encoder;

    TestDataLoader(UserRepository userRepository, Encoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        userRepository.save(new User(T_ADMIN.getUsername(), encoder.hashPassword(T_ADMIN.getPassword()), UserRole.ADMIN));
    }
}
