package com.example.gistcompetitioncnserver.post;

import com.example.gistcompetitioncnserver.exception.post.DuplicatedAgreementException;
import com.example.gistcompetitioncnserver.exception.post.PostException;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostTest {
    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = new User(1L, "email@email.com", "password", UserRole.USER, true);
        post = new Post("title", "description", "category", user.getId());
    }

    @Test
    void agree() {
        assertThat(post.getAgreements()).hasSize(0);
        post.applyAgreement(user);
        assertThat(post.getAgreements()).hasSize(1);
    }

    @Test
    void agreeTwiceFailTest() {
        post.applyAgreement(user);
        assertThatThrownBy(
                () -> post.applyAgreement(user)
        ).isInstanceOf(DuplicatedAgreementException.class);
    }

    @Test
    void agreeByMultipleUser() {
        User user = new User(2L, "email@email.com", "password", UserRole.USER, true);
        User user3 = new User(3L, "email@email.com", "password", UserRole.USER, true);
        post.applyAgreement(this.user);
        post.applyAgreement(user);
        post.applyAgreement(user3);
        assertThat(post.getAgreements()).hasSize(3);
    }


}
