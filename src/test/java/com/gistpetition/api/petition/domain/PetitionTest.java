package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.*;
import com.gistpetition.api.petition.PetitionBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.LongStream;

import static com.gistpetition.api.petition.domain.Petition.REQUIRED_AGREEMENT_FOR_ANSWER;
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
    public static final String ANSWER_CONTENT = "답변을 달았습니다.";

    private Petition petition;

    @BeforeEach
    void setUp() {
        petition = PetitionBuilder.aPetition().withExpiredAt(PETITION_EXPIRED_AT).withTempUrl(TEMP_URL).build();
    }

    @Test
    void agree() {
        petition.agree(1L, AGREEMENT_DESCRIPTION, PETITION_ONGOING_AT);

        assertThat(petition.getAgreeCount()).isEqualTo(1);
    }

    @Test
    void agreeByMultipleUser() {
        petition.agree(1L, AGREEMENT_DESCRIPTION, PETITION_ONGOING_AT);
        petition.agree(2L, AGREEMENT_DESCRIPTION, PETITION_ONGOING_AT);
        petition.agree(3L, AGREEMENT_DESCRIPTION, PETITION_ONGOING_AT);

        assertThat(petition.getAgreeCount()).isEqualTo(3);
    }

    @Test
    void agreeExpiredPetition() {
        assertThatThrownBy(() ->
                petition.agree(1L, AGREEMENT_DESCRIPTION, PETITION_EXPIRED_AT.plusSeconds(1))
        ).isInstanceOf(ExpiredPetitionException.class);
    }

    @Test
    void release() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_RELEASE);

        petition.release(PETITION_ONGOING_AT);

        assertTrue(petition.isReleased());
    }

    @Test
    void releaseAlreadyReleased() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_RELEASE);
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
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_RELEASE);

        petition.release(PETITION_ONGOING_AT);

        assertThatThrownBy(() ->
                petition.release(PETITION_EXPIRED_AT.plusSeconds(1))
        ).isInstanceOf(ExpiredPetitionException.class);
    }

    @Test
    void cancelRelease() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_RELEASE);
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

    @Test
    void answer() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_ANSWER);
        petition.release(PETITION_ONGOING_AT);

        petition.answer(ANSWER_CONTENT);

        assertTrue(petition.isAnswered());
        assertThat(petition.getAnswer2().getContent()).isEqualTo(ANSWER_CONTENT);
    }

    @Test
    void answer_for_not_released_petition() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_ANSWER);

        assertThatThrownBy(() ->
                petition.answer(ANSWER_CONTENT)
        ).isInstanceOf(NotReleasedPetitionException.class);

        assertFalse(petition.isAnswered());
    }

    @Test
    void answer_for_not_enough_agreed_petition() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_RELEASE);
        petition.release(PETITION_ONGOING_AT);

        assertThatThrownBy(() ->
                petition.answer(ANSWER_CONTENT)
        ).isInstanceOf(NotEnoughAgreementException.class);

        assertFalse(petition.isAnswered());
    }

    @Test
    void update_answer() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_ANSWER);
        petition.release(PETITION_ONGOING_AT);
        petition.answer(ANSWER_CONTENT);

        String updateAnswerContent = "답변 수정을 진행했다.";
        petition.updateAnswer(updateAnswerContent);

        assertThat(petition.getAnswer2().getContent()).isEqualTo(updateAnswerContent);
    }

    @Test
    void update_answer_not_answered_petition() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_ANSWER);
        petition.release(PETITION_ONGOING_AT);

        String updateAnswerContent = "답변 수정을 진행했다.";
        assertThatThrownBy(
                () -> petition.updateAnswer(updateAnswerContent)
        ).isInstanceOf(NotAnsweredPetitionException.class);
    }

    @Test
    void delete_answer() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_ANSWER);
        petition.release(PETITION_ONGOING_AT);
        petition.answer(ANSWER_CONTENT);

        petition.deleteAnswer();

        assertFalse(petition.isAnswered());
    }

    @Test
    void delete_answer_not_answered_petition() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_ANSWER);
        petition.release(PETITION_ONGOING_AT);

        assertThatThrownBy(() -> petition.deleteAnswer()).isInstanceOf(NotAnsweredPetitionException.class);
    }

    private void agreePetitionByMultipleUsers(Petition target, int numberOfUsers) {
        LongStream.range(0, numberOfUsers)
                .forEach(userId -> target.agree(userId, AGREEMENT_DESCRIPTION, PETITION_ONGOING_AT));
    }
}
