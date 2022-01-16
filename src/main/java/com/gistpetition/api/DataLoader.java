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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DataLoader implements CommandLineRunner {
    private final User ADMIN = new User(1L, "admin@gist.ac.kr", new BcryptEncoder().hashPassword("test1234!"), UserRole.ADMIN);
    private final UserRepository userRepository;
    private final PetitionRepository petitionRepository;

    @Override
    public void run(String... args) {
        userRepository.save(ADMIN);

        List<Petition> petitionsAboutDorm = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            petitionsAboutDorm.add(new Petition("dorm " + i, "description" + i, Category.DORMITORY, 1L));
        }

        List<Petition> petitionsAboutFacility = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            petitionsAboutFacility.add(new Petition("professor " + i, "description" + i, Category.FACILITY, 1L));
        }

        petitionRepository.saveAll(petitionsAboutDorm);
        petitionRepository.saveAll(petitionsAboutFacility);
    }
}
