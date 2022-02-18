package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.AlreadyReleasedPetitionException;
import com.gistpetition.api.exception.petition.NotEnoughAgreementException;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PetitionTest {
    private static final String AGREEMENT_DESCRIPTION = "동의합니다.";
    public static final String EMAIL = "email@gist.ac.kr";
    public static final String PASSWORD = "password";

    private User user;
    private Petition petition;

    @BeforeEach
    void setUp() {
        user = new User(1L, EMAIL, PASSWORD, UserRole.USER);
        petition = new Petition("title", "description", Category.DORMITORY, user.getId());
    }

    @Test
    void agree() {
        assertThat(petition.getAgreements()).hasSize(0);
        Agreement agreement = new Agreement(AGREEMENT_DESCRIPTION, user.getId());
        petition.addAgreement(agreement);
        assertThat(petition.getAgreements()).hasSize(1);
        assertThat(petition.getAgreeCount()).isEqualTo(1);
    }

    @Test
    void agreeByMultipleUser() {
        User user1 = new User(2L, EMAIL, PASSWORD, UserRole.USER);
        User user2 = new User(3L, EMAIL, PASSWORD, UserRole.USER);
        petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, user.getId()));
        petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, user1.getId()));
        petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, user2.getId()));
        assertThat(petition.getAgreements()).hasSize(3);
        assertThat(petition.getAgreeCount()).isEqualTo(3);
    }

    @Test
    void release() {
        LongStream.range(0, 5)
                .mapToObj(i -> new User(i, i + EMAIL, PASSWORD, UserRole.USER))
                .forEach(u -> petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, u.getId())));

        petition.release();

        assertTrue(petition.isReleased());
    }

    @Test
    void releaseAlreadyReleased() {
        LongStream.range(0, 5)
                .mapToObj(i -> new User(i, i + EMAIL, PASSWORD, UserRole.USER))
                .forEach(u -> petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, u.getId())));
        petition.release();

        assertThatThrownBy(() -> petition.release()).isInstanceOf(AlreadyReleasedPetitionException.class);
    }

    @Test
    void releaseNotEnoughAgreement() {
        assertThatThrownBy(() -> petition.release()).isInstanceOf(NotEnoughAgreementException.class);
    }
}
