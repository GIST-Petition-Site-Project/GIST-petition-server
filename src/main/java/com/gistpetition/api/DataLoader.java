package com.gistpetition.api;

import com.gistpetition.api.post.domain.Post;
import com.gistpetition.api.post.domain.Category;
import com.gistpetition.api.post.domain.PostRepository;
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
    private final PostRepository postRepository;

    @Override
    public void run(String... args) {
        userRepository.save(ADMIN);

        List<Post> postsAboutDorm = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            postsAboutDorm.add(new Post("dorm " + i, "description" + i, Category.DORMITORY, 1L));
        }

        List<Post> postsAboutFacility = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            postsAboutFacility.add(new Post("professor " + i, "description" + i, Category.FACILITY, 1L));
        }

        postRepository.saveAll(postsAboutDorm);
        postRepository.saveAll(postsAboutFacility);
    }
}
