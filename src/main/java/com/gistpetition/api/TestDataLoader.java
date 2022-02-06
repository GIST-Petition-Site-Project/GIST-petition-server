package com.gistpetition.api;

import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.Petition;
import com.gistpetition.api.petition.domain.PetitionRepository;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import com.gistpetition.api.utils.password.BcryptEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Profile("!dev && !prod")
@RequiredArgsConstructor
@Component
public class TestDataLoader implements CommandLineRunner {
    private final User ADMIN = new User(1L, "admin@gist.ac.kr", new BcryptEncoder().hashPassword("test1234!"), UserRole.ADMIN);
    private final User MANAGER = new User(2L, "manager@gist.ac.kr", new BcryptEncoder().hashPassword("test1234!"), UserRole.MANAGER);
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        userRepository.save(ADMIN);
        userRepository.save(MANAGER);
    }
}
