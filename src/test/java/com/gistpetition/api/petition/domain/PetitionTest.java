package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.DuplicatedAgreementException;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PetitionTest {
    private static final String AGREEMENT_DESCRIPTION = "동의합니다.";

    private User user;
    private Petition petition;

    @BeforeEach
    void setUp() {
        user = new User(1L, "email@email.com", "password", UserRole.USER);
        petition = new Petition("title", "description", Category.DORMITORY, user.getId());
    }

    @Test
    void agree() {
        assertThat(petition.getAgreements()).hasSize(0);
        Agreement agreement = new Agreement(AGREEMENT_DESCRIPTION, user.getId());
        petition.addAgreement(agreement);
        assertThat(petition.getAgreements()).hasSize(1);
    }

    @Test
    void agreeByMultipleUser() {
        User user1 = new User(2L, "email@email.com", "password", UserRole.USER);
        User user2 = new User(3L, "email@email.com", "password", UserRole.USER);
        petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, user.getId()));
        petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, user1.getId()));
        petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, user2.getId()));
        assertThat(petition.getAgreements()).hasSize(3);
    }
}
