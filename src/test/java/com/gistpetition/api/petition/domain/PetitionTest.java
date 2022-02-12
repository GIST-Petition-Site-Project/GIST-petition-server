package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.DuplicatedAgreementException;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

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
        petition.applyAgreement(user, AGREEMENT_DESCRIPTION);
        assertThat(petition.getAgreements()).hasSize(1);
        assertThat(petition.getAgreeCount()).isEqualTo(1);
    }

    @Test
    void agreeTwiceFailTest() {
        petition.applyAgreement(user, AGREEMENT_DESCRIPTION);
        assertThatThrownBy(
                () -> petition.applyAgreement(user, AGREEMENT_DESCRIPTION)
        ).isInstanceOf(DuplicatedAgreementException.class);
    }

    @Test
    void agreeByMultipleUser() {
        User user = new User(2L, "email@email.com", "password", UserRole.USER);
        User user3 = new User(3L, "email@email.com", "password", UserRole.USER);
        petition.applyAgreement(this.user, AGREEMENT_DESCRIPTION);
        petition.applyAgreement(user, AGREEMENT_DESCRIPTION);
        petition.applyAgreement(user3, AGREEMENT_DESCRIPTION);
        assertThat(petition.getAgreements()).hasSize(3);
    }
}
