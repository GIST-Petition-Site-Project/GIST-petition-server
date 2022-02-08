package com.gistpetition.api.petition;

import com.gistpetition.api.exception.petition.DuplicatedAgreementException;
import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.Petition;
import com.gistpetition.api.petition.dto.AgreementRequest;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PetitionTest {
    private static final AgreementRequest AGREEMENT_REQUEST = new AgreementRequest("동의합니다.");

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
        petition.applyAgreement(user, AGREEMENT_REQUEST.getContent());
        Assertions.assertThat(petition.getAgreements()).hasSize(1);
    }

    @Test
    void agreeTwiceFailTest() {
        petition.applyAgreement(user, AGREEMENT_REQUEST.getContent());
        assertThatThrownBy(
                () -> petition.applyAgreement(user, AGREEMENT_REQUEST.getContent())
        ).isInstanceOf(DuplicatedAgreementException.class);
    }

    @Test
    void agreeByMultipleUser() {
        User user = new User(2L, "email@email.com", "password", UserRole.USER);
        User user3 = new User(3L, "email@email.com", "password", UserRole.USER);
        petition.applyAgreement(this.user, AGREEMENT_REQUEST.getContent());
        petition.applyAgreement(user, AGREEMENT_REQUEST.getContent());
        petition.applyAgreement(user3, AGREEMENT_REQUEST.getContent());
        Assertions.assertThat(petition.getAgreements()).hasSize(3);
    }


}
