package com.example.gistcompetitioncnserver.post;

import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostTest {
    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = new User(1L, "userName", "email@email.com", "password", false, UserRole.USER, true);
        post = new Post("title", "description", "category", user.getId());
    }

    @Test
    void agree() {
        post.applyAgreement(user);
        assertThat(post.getAgreements()).hasSize(1);
    }

    @Test
    void disagree() {
        post.applyAgreement(user);
        assertThat(post.getAgreements()).hasSize(1);
        post.applyAgreement(user);
        assertThat(post.getAgreements()).hasSize(0);
    }

    @Test
    void agreeByMultipleUser() {
        User user2 = new User(2L, "userName", "email@email.com", "password", false, UserRole.USER, true);
        User user3 = new User(3L, "userName", "email@email.com", "password", false, UserRole.USER, true);
        post.applyAgreement(user);
        post.applyAgreement(user2);
        post.applyAgreement(user3);
        assertThat(post.getAgreements()).hasSize(3);
    }
}
