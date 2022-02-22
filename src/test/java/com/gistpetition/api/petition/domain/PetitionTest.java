package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.AlreadyReleasedPetitionException;
import com.gistpetition.api.exception.petition.ExpiredPetitionException;
import com.gistpetition.api.exception.petition.NotEnoughAgreementException;
import com.gistpetition.api.petition.PetitionBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PetitionTest {
    public static final LocalDateTime PETITION_CREATION_AT = LocalDateTime.of(2002, 2, 2, 2, 2);
    public static final LocalDateTime PETITION_ONGOING_AT = PETITION_CREATION_AT.plusDays(Petition.POSTING_PERIOD / 2);
    public static final LocalDateTime PETITION_EXPIRED_AT = PETITION_CREATION_AT.plusDays(Petition.POSTING_PERIOD);
    private static final String AGREEMENT_DESCRIPTION = "동의합니다.";
    private static final String TEMP_URL = "AAAAAA";

    private Petition petition;

    @BeforeEach
    void setUp() {
        petition = PetitionBuilder.aPetition().withExpiredAt(PETITION_EXPIRED_AT).withTempUrl(TEMP_URL).build();
    }

    @Test
    void agree() {
        assertThat(petition.getAgreements()).hasSize(0);
        Agreement agreement = new Agreement(AGREEMENT_DESCRIPTION, 1L);
        petition.addAgreement(agreement, PETITION_ONGOING_AT);
        assertThat(petition.getAgreements()).hasSize(1);
        assertThat(petition.getAgreeCount()).isEqualTo(1);
    }

    @Test
    void agreeByMultipleUser() {
        petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, 1L), PETITION_ONGOING_AT);
        petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, 2L), PETITION_ONGOING_AT);
        petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, 3L), PETITION_ONGOING_AT);
        assertThat(petition.getAgreements()).hasSize(3);
        assertThat(petition.getAgreeCount()).isEqualTo(3);
    }

    @Test
    void agreeExpiredPetition() {
        assertThatThrownBy(() ->
                petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, 1L), PETITION_EXPIRED_AT.plusDays(1))
        ).isInstanceOf(ExpiredPetitionException.class);
    }

    @Test
    void release() {
        agreedUponBy(Petition.REQUIRED_AGREEMENT_FOR_RELEASE);

        assertTrue(petition.isReleased());
    }

    @Test
    void releaseAlreadyReleased() {
        agreedUponBy(Petition.REQUIRED_AGREEMENT_FOR_RELEASE);

        assertThatThrownBy(() -> petition.release(PETITION_ONGOING_AT.plusSeconds(1))
        ).isInstanceOf(AlreadyReleasedPetitionException.class);
    }

    @Test
    void releaseNotEnoughAgreement() {
        assertThatThrownBy(() -> petition.release(PETITION_ONGOING_AT)
        ).isInstanceOf(NotEnoughAgreementException.class);
    }

    @Test
    void releaseExpiredPetition() {
        agreedUponBy(Petition.REQUIRED_AGREEMENT_FOR_RELEASE);

        assertThatThrownBy(() ->
                petition.release(PETITION_EXPIRED_AT.plusDays(1))
        ).isInstanceOf(ExpiredPetitionException.class);
    }

    private void agreedUponBy(int numOfUsers) {
        LongStream.range(0, numOfUsers)
                .forEach(userId -> petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, userId), PETITION_ONGOING_AT));
        petition.release(PETITION_ONGOING_AT);
    }
}
