package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.*;
import com.gistpetition.api.petition.PetitionBuilder;
import com.gistpetition.api.utils.urlmatcher.UrlMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static com.gistpetition.api.petition.domain.Petition.REQUIRED_AGREEMENT_FOR_ANSWER;
import static com.gistpetition.api.petition.domain.Petition.REQUIRED_AGREEMENT_FOR_RELEASE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PetitionTest {
    public static final Instant PETITION_CREATION_AT = LocalDateTime.of(2002, 2, 2, 2, 2).toInstant(ZoneOffset.UTC);
    public static final Instant PETITION_NOT_EXPIRED_AT = PETITION_CREATION_AT.plusSeconds(Petition.POSTING_PERIOD_BY_SECONDS / 2);
    public static final Instant PETITION_EXPIRED_AT = PETITION_CREATION_AT.plusSeconds(Petition.POSTING_PERIOD_BY_SECONDS);
    private static final String AGREEMENT_DESCRIPTION = "동의합니다.";
    private static final String TEMP_URL = "AAAAAA";
    private static final String ANSWER_DESCRIPTION = "답변을 달았습니다.";
    private static final UrlMatcher ALWAYS_TRUE_URL_MATCHER = url -> true;
    public static final String REJECT_DESCRIPTION = "description";

    private Petition petition;

    @BeforeEach
    void setUp() {
        petition = PetitionBuilder.aPetition().build();
        petition.placeTemporary(TEMP_URL, PETITION_CREATION_AT);
    }

    private static Stream<Arguments> create_petition_invalid_condition() {
        return Stream.of(
                Arguments.arguments("", "description", InvalidTitleLengthException.class),
                Arguments.arguments("A".repeat(Title.TITLE_MAX_LENGTH + 1), "description", InvalidTitleLengthException.class),
                Arguments.arguments("title", "", InvalidDescriptionLengthException.class),
                Arguments.arguments("title", "A".repeat(Description.DESCRIPTION_MAX_LENGTH + 1), InvalidDescriptionLengthException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("create_petition_invalid_condition")
    void create_petition_invalid_condition(String title, String description, Class<Exception> exceptionClass) {
        assertThatThrownBy(
                () -> new Petition(title, description, Category.DORMITORY, 1L)
        ).isInstanceOf(exceptionClass);
    }

    @Test
    void placeTemporary() {
        Petition petitionA = new Petition("title", "description", Category.DORMITORY, 1L);
        petitionA.placeTemporary(TEMP_URL, PETITION_CREATION_AT);

        assertTrue(petitionA.isTemporary());
        assertThat(petitionA.getTempUrl()).isEqualTo(TEMP_URL);
        assertThat(petitionA.getExpiredAt()).isEqualTo(PETITION_EXPIRED_AT);
    }

    @Test
    void agree() {
        petition.agree(1L, AGREEMENT_DESCRIPTION, PETITION_NOT_EXPIRED_AT);

        assertThat(petition.getAgreeCount()).isEqualTo(1);
    }

    @Test
    void agreeByMultipleUser() {
        petition.agree(1L, AGREEMENT_DESCRIPTION, PETITION_NOT_EXPIRED_AT);
        petition.agree(2L, AGREEMENT_DESCRIPTION, PETITION_NOT_EXPIRED_AT);
        petition.agree(3L, AGREEMENT_DESCRIPTION, PETITION_NOT_EXPIRED_AT);

        assertThat(petition.getAgreeCount()).isEqualTo(3);
        assertThat(petition.getWaitingForAnswerAt()).isNull();
    }

    @Test
    void agreeByWaitingForAnswerTimes() {
        for (long i = 0; i < REQUIRED_AGREEMENT_FOR_ANSWER; i++) {
            petition.agree(i, AGREEMENT_DESCRIPTION, PETITION_NOT_EXPIRED_AT);
        }

        assertThat(petition.getAgreeCount()).isEqualTo(REQUIRED_AGREEMENT_FOR_ANSWER);
        assertThat(petition.getWaitingForAnswerAt()).isEqualTo(PETITION_NOT_EXPIRED_AT);
    }

    @Test
    void agreeExpiredPetition() {
        assertThatThrownBy(() ->
                petition.agree(1L, AGREEMENT_DESCRIPTION, PETITION_EXPIRED_AT.plusSeconds(1))
        ).isInstanceOf(ExpiredPetitionException.class);
    }

    @Test
    void agree_to_rejectedPetition() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_RELEASE);
        petition.reject(REJECT_DESCRIPTION, PETITION_NOT_EXPIRED_AT);

        assertThatThrownBy(
                () -> petition.agree(1L, AGREEMENT_DESCRIPTION, PETITION_NOT_EXPIRED_AT)
        ).isInstanceOf(AlreadyRejectedPetitionException.class);
    }


    @Test
    void release() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_RELEASE);

        petition.release(PETITION_NOT_EXPIRED_AT);

        assertTrue(petition.isReleased());
    }

    @Test
    void releaseAlreadyReleased() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_RELEASE);
        petition.release(PETITION_NOT_EXPIRED_AT);

        assertThatThrownBy(
                () -> petition.release(PETITION_NOT_EXPIRED_AT.plusSeconds(1))
        ).isInstanceOf(NotValidStatusToReleasePetitionException.class);
    }

    @Test
    void releaseNotEnoughAgreement() {
        assertThatThrownBy(() -> petition.release(PETITION_NOT_EXPIRED_AT)
        ).isInstanceOf(NotEnoughAgreementException.class);
    }

    @Test
    void releaseExpiredPetition() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_RELEASE);

        petition.release(PETITION_NOT_EXPIRED_AT);

        assertThatThrownBy(() ->
                petition.release(PETITION_EXPIRED_AT.plusSeconds(1))
        ).isInstanceOf(ExpiredPetitionException.class);
    }

    @Test
    void cancelRelease() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_RELEASE);
        petition.release(PETITION_NOT_EXPIRED_AT);

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
    void reject_temporary_success() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_RELEASE);

        petition.reject(REJECT_DESCRIPTION, PETITION_NOT_EXPIRED_AT);

        assertTrue(petition.isRejected());
        assertThat(petition.getRejection().getDescription()).isEqualTo(REJECT_DESCRIPTION);
    }

    @Test
    void reject_released_failed() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_RELEASE);
        petition.release(PETITION_NOT_EXPIRED_AT);

        assertThatThrownBy(() -> petition.reject(REJECT_DESCRIPTION, PETITION_NOT_EXPIRED_AT))
                .isInstanceOf(NotValidStatusToRejectPetitionException.class);
    }


    @Test
    void reject_already_rejected() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_RELEASE);
        petition.reject(REJECT_DESCRIPTION, PETITION_NOT_EXPIRED_AT);

        assertThatThrownBy(
                () -> petition.reject(REJECT_DESCRIPTION, PETITION_NOT_EXPIRED_AT)
        ).isInstanceOf(NotValidStatusToRejectPetitionException.class);
    }

    @Test
    void reject_answered() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_ANSWER);
        petition.release(PETITION_NOT_EXPIRED_AT);
        petition.answer(ANSWER_DESCRIPTION, null, ALWAYS_TRUE_URL_MATCHER);

        assertThatThrownBy(
                () -> petition.reject(REJECT_DESCRIPTION, PETITION_NOT_EXPIRED_AT)
        ).isInstanceOf(NotValidStatusToRejectPetitionException.class);
    }

    @Test
    void reject_expired() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_RELEASE);
        assertThatThrownBy(
                () -> petition.reject(REJECT_DESCRIPTION, PETITION_EXPIRED_AT.plusSeconds(1))
        ).isInstanceOf(ExpiredPetitionException.class);
    }

    @Test
    void update_rejection() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_ANSWER);
        petition.reject(REJECT_DESCRIPTION, PETITION_NOT_EXPIRED_AT);

        String updateRejectionDescription = "반려 내용 수정을 진행했다.";
        petition.updateRejection(updateRejectionDescription);

        assertThat(petition.getRejection().getDescription()).isEqualTo(updateRejectionDescription);
    }

    @Test
    void cancel_rejection() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_ANSWER);
        petition.reject(REJECT_DESCRIPTION, PETITION_NOT_EXPIRED_AT);

        petition.cancelRejection();

        assertFalse(petition.isRejected());
    }

    @Test
    void answer() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_ANSWER);
        petition.release(PETITION_NOT_EXPIRED_AT);

        petition.answer(ANSWER_DESCRIPTION, null, ALWAYS_TRUE_URL_MATCHER);

        assertTrue(petition.isAnswered());
        assertThat(petition.getAnswer().getDescription()).isEqualTo(ANSWER_DESCRIPTION);
    }

    @Test
    void answer_for_already_answered_petition() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_ANSWER);
        petition.release(PETITION_NOT_EXPIRED_AT);
        petition.answer(ANSWER_DESCRIPTION, null, ALWAYS_TRUE_URL_MATCHER);

        assertThatThrownBy(
                () -> petition.answer(ANSWER_DESCRIPTION, null, ALWAYS_TRUE_URL_MATCHER)
        ).isInstanceOf(NotValidStatusToAnswerPetitionException.class);
    }

    @Test
    void answer_for_not_released_petition() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_ANSWER);

        assertThatThrownBy(() ->
                petition.answer(ANSWER_DESCRIPTION, null, ALWAYS_TRUE_URL_MATCHER)
        ).isInstanceOf(NotValidStatusToAnswerPetitionException.class);

        assertFalse(petition.isAnswered());
    }

    @Test
    void answer_for_not_enough_agreed_petition() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_RELEASE);
        petition.release(PETITION_NOT_EXPIRED_AT);

        assertThatThrownBy(() ->
                petition.answer(ANSWER_DESCRIPTION, null, ALWAYS_TRUE_URL_MATCHER)
        ).isInstanceOf(NotEnoughAgreementException.class);

        assertFalse(petition.isAnswered());
    }

    @Test
    void answer_to_rejectedPetition() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_RELEASE);
        petition.reject(REJECT_DESCRIPTION, PETITION_NOT_EXPIRED_AT);

        assertThatThrownBy(() ->
                petition.answer(ANSWER_DESCRIPTION, null, ALWAYS_TRUE_URL_MATCHER)
        ).isInstanceOf(NotValidStatusToAnswerPetitionException.class);
    }

    @Test
    void update_answer() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_ANSWER);
        petition.release(PETITION_NOT_EXPIRED_AT);
        petition.answer(ANSWER_DESCRIPTION, null, ALWAYS_TRUE_URL_MATCHER);

        String updateAnswerContent = "답변 수정을 진행했다.";
        petition.updateAnswer(updateAnswerContent, null, ALWAYS_TRUE_URL_MATCHER);

        assertThat(petition.getAnswer().getDescription()).isEqualTo(updateAnswerContent);
    }

    @Test
    void update_answer_not_answered_petition() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_ANSWER);
        petition.release(PETITION_NOT_EXPIRED_AT);

        String updateAnswerContent = "답변 수정을 진행했다.";
        assertThatThrownBy(
                () -> petition.updateAnswer(updateAnswerContent, null, ALWAYS_TRUE_URL_MATCHER)
        ).isInstanceOf(NotAnsweredPetitionException.class);
    }

    @Test
    void delete_answer() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_ANSWER);
        petition.release(PETITION_NOT_EXPIRED_AT);
        petition.answer(ANSWER_DESCRIPTION, null, ALWAYS_TRUE_URL_MATCHER);

        petition.deleteAnswer();

        assertFalse(petition.isAnswered());
    }

    @Test
    void delete_answer_not_answered_petition() {
        agreePetitionByMultipleUsers(petition, REQUIRED_AGREEMENT_FOR_ANSWER);
        petition.release(PETITION_NOT_EXPIRED_AT);

        assertThatThrownBy(() -> petition.deleteAnswer()).isInstanceOf(NotAnsweredPetitionException.class);
    }

    private void agreePetitionByMultipleUsers(Petition target, int numberOfUsers) {
        LongStream.range(0, numberOfUsers)
                .forEach(userId -> target.agree(userId, AGREEMENT_DESCRIPTION, PETITION_NOT_EXPIRED_AT));
    }
}
