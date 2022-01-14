package com.gistpetition.api.post;

import com.gistpetition.api.exception.post.DuplicatedAgreementException;
import com.gistpetition.api.post.domain.Category;
import com.gistpetition.api.post.domain.Post;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostTest {
    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = new User(1L, "email@email.com", "password", UserRole.USER);
        post = new Post("title", "description", Category.DORMITORY, user.getId());
    }

    @Test
    void agree() {
        Assertions.assertThat(post.getAgreements()).hasSize(0);
        post.applyAgreement(user);
        Assertions.assertThat(post.getAgreements()).hasSize(1);
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
        User user = new User(2L, "email@email.com", "password", UserRole.USER);
        User user3 = new User(3L, "email@email.com", "password", UserRole.USER);
        post.applyAgreement(this.user);
        post.applyAgreement(user);
        post.applyAgreement(user3);
        Assertions.assertThat(post.getAgreements()).hasSize(3);
    }


}
