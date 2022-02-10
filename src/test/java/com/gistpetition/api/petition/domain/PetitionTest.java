package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.DuplicatedAgreementException;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PetitionTest {
    private User user;
    private Petition petition;

    @BeforeEach
    void setUp() {
        user = new User(1L, "email@email.com", "password", UserRole.USER);
        petition = new Petition("title", "description", Category.DORMITORY, user.getId());
    }

    @Test
    void agree() {
        Assertions.assertThat(petition.getAgreements()).hasSize(0);
        petition.applyAgreement(user);
        Assertions.assertThat(petition.getAgreements()).hasSize(1);
    }

    @Test
    void agreeTwiceFailTest() {
        petition.applyAgreement(user);
        assertThatThrownBy(
                () -> petition.applyAgreement(user)
        ).isInstanceOf(DuplicatedAgreementException.class);
    }

    @Test
    void agreeByMultipleUser() {
        User user = new User(2L, "email@email.com", "password", UserRole.USER);
        User user3 = new User(3L, "email@email.com", "password", UserRole.USER);
        petition.applyAgreement(this.user);
        petition.applyAgreement(user);
        petition.applyAgreement(user3);
        Assertions.assertThat(petition.getAgreements()).hasSize(3);
    }


}
