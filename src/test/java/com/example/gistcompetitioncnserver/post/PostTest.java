package com.example.gistcompetitioncnserver.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostTest {
    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = new User(1L, "userName", "email@email.com", "password", false, UserRole.USER, true);
        post = new Post("title", "description", "category", user.getId());
    }

    @Test
    void like() {
        post.applyLike(user);
        assertThat(post.getLikes()).hasSize(1);
    }

    @Test
    void unlike() {
        post.applyLike(user);
        assertThat(post.getLikes()).hasSize(1);
        post.applyLike(user);
        assertThat(post.getLikes()).hasSize(0);
    }

    @Test
    void likeByMultipleUser() {
        User user2 = new User(2L, "userName", "email@email.com", "password", false, UserRole.USER, true);
        User user3 = new User(3L, "userName", "email@email.com", "password", false, UserRole.USER, true);
        post.applyLike(user);
        post.applyLike(user2);
        post.applyLike(user3);
        assertThat(post.getLikes()).hasSize(3);
    }
}
