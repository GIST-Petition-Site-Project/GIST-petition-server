package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.AlreadyReleasedPetitionException;
import com.gistpetition.api.exception.petition.ExpiredPetitionException;
import com.gistpetition.api.exception.petition.NotEnoughAgreementException;
import com.gistpetition.api.exception.petition.NotReleasedPetitionException;
import com.gistpetition.api.petition.PetitionBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.LongStream;

import static com.gistpetition.api.petition.domain.Petition.REQUIRED_AGREEMENT_FOR_RELEASE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PetitionTest {
    public static final Instant PETITION_CREATION_AT = LocalDateTime.of(2002, 2, 2, 2, 2).toInstant(ZoneOffset.UTC);
    public static final Instant PETITION_ONGOING_AT = PETITION_CREATION_AT.plusSeconds(Petition.POSTING_PERIOD_BY_SECONDS / 2);
    public static final Instant PETITION_EXPIRED_AT = PETITION_CREATION_AT.plusSeconds(Petition.POSTING_PERIOD_BY_SECONDS);
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
                petition.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, 1L), PETITION_EXPIRED_AT.plusSeconds(1))
        ).isInstanceOf(ExpiredPetitionException.class);
    }

    @Test
    void release() {
        agreePetitionBy(petition, REQUIRED_AGREEMENT_FOR_RELEASE);

        petition.release(PETITION_ONGOING_AT);

        assertTrue(petition.isReleased());
    }

    @Test
    void releaseAlreadyReleased() {
        agreePetitionBy(petition, REQUIRED_AGREEMENT_FOR_RELEASE);
        petition.release(PETITION_ONGOING_AT);

        assertThatThrownBy(
                () -> petition.release(PETITION_ONGOING_AT.plusSeconds(1))
        ).isInstanceOf(AlreadyReleasedPetitionException.class);
    }

    @Test
    void releaseNotEnoughAgreement() {
        assertThatThrownBy(() -> petition.release(PETITION_ONGOING_AT)
        ).isInstanceOf(NotEnoughAgreementException.class);
    }

    @Test
    void releaseExpiredPetition() {
        agreePetitionBy(petition, REQUIRED_AGREEMENT_FOR_RELEASE);

        petition.release(PETITION_ONGOING_AT);

        assertThatThrownBy(() ->
                petition.release(PETITION_EXPIRED_AT.plusSeconds(1))
        ).isInstanceOf(ExpiredPetitionException.class);
    }

    @Test
    void cancelRelease() {
        agreePetitionBy(petition, REQUIRED_AGREEMENT_FOR_RELEASE);
        petition.release(PETITION_ONGOING_AT);

        petition.cancelRelease();

        assertFalse(petition.isReleased());
    }

    @Test
    void cancelReleaseIfNotReleased() {
        assertThatThrownBy(
                () -> petition.cancelRelease()
        ).isInstanceOf(NotReleasedPetitionException.class);
    }

    private void agreePetitionBy(Petition target, int numOfUsers) {
        LongStream.range(0, numOfUsers)
                .forEach(userId -> target.addAgreement(new Agreement(AGREEMENT_DESCRIPTION, userId), PETITION_ONGOING_AT));
    }
}
