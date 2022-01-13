package com.example.gistcompetitioncnserver;

import com.example.gistcompetitioncnserver.common.password.BcryptEncoder;
import com.example.gistcompetitioncnserver.user.domain.User;
import com.example.gistcompetitioncnserver.user.domain.UserRepository;
import com.example.gistcompetitioncnserver.user.domain.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final User ADMIN = new User(1L, "admin@gist.ac.kr", new BcryptEncoder().hashPassword("test1234!"), UserRole.ADMIN);

    private final UserRepository userRepository;

    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        userRepository.save(ADMIN);
    }
}
