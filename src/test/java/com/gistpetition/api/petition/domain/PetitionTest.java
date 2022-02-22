package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.AlreadyReleasedPetitionException;
import com.gistpetition.api.exception.petition.ExpiredPetitionException;
import com.gistpetition.api.exception.petition.NotEnoughAgreementException;
import com.gistpetition.api.petition.PetitionBuilder;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PetitionTest {
    private static final String AGREEMENT_DESCRIPTION = "동의합니다.";
    private static final String TEMP_URL = "AAAAAA";

    private Petition petition;

    @BeforeEach
    void setUp() {
        petition = new Petition("title", "description", Category.DORMITORY, 1L, TEMP_URL);
    }

    @Test
    void agree() {
        assertThat(petition.getAgreements()).hasSize(0);
        Agreement agreement = new Agreement(AGREEMENT_DESCRIPTION, 1L);
        petition.addAgreement(agreement);
        assertThat(petition.getAgreements()).hasSize(1);
        assertThat(petition.getAgreeCount()).isEqualTo(1);
    }

    @Test
    void agreeByMultipleUser() {
        petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, 1L));
        petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, 2L));
        petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, 3L));
        assertThat(petition.getAgreements()).hasSize(3);
        assertThat(petition.getAgreeCount()).isEqualTo(3);
    }

    @Test
    void agreeExpiredPetition() {
        LocalDateTime past = LocalDateTime.MIN;
        Petition expiredPetition = PetitionBuilder.aPetition().withExpiredAt(past).build();
        assertThatThrownBy(() ->
                expiredPetition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, 1L))
        ).isInstanceOf(ExpiredPetitionException.class);
    }

    @Test
    void release() {
        LongStream.range(0, 5)
                .forEach(userId -> petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, userId)));

        petition.release();

        assertTrue(petition.isReleased());
    }

    @Test
    void releaseAlreadyReleased() {
        LongStream.range(0, 5)
                .forEach(userId -> petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, userId)));
        petition.release();

        assertThatThrownBy(() -> petition.release()).isInstanceOf(AlreadyReleasedPetitionException.class);
    }

    @Test
    void releaseNotEnoughAgreement() {
        assertThatThrownBy(() -> petition.release()).isInstanceOf(NotEnoughAgreementException.class);
    }
}
