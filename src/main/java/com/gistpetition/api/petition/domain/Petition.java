package com.gistpetition.api.petition.domain;

import com.gistpetition.api.common.persistence.BaseEntity;
import com.gistpetition.api.exception.petition.*;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.utils.urlmatcher.UrlMatcher;
import lombok.Getter;
import org.hibernate.annotations.OptimisticLock;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.Instant;

import static com.gistpetition.api.petition.domain.Status.*;

@Audited
@Getter
@Entity
public class Petition extends BaseEntity {
    public static int REQUIRED_AGREEMENT_FOR_RELEASE = 5;
    public static int REQUIRED_AGREEMENT_FOR_ANSWER = 50;
    public static final int POSTING_PERIOD_BY_SECONDS = 30 * 24 * 60 * 60;

    @Embedded
    private Title title;
    @Embedded
    private Description description;
    @Enumerated(EnumType.STRING)
    private Category category;
    @NotAudited
    @Enumerated(EnumType.STRING)
    private Status status;
    @NotAudited
    private Boolean released = false;
    @NotAudited
    private Instant waitingForAnswerAt;
    private Instant expiredAt;
    private Long userId;
    @Column(unique = true)
    private String tempUrl;
    @Embedded
    @OptimisticLock(excluded = true)
    private final Agreements agreements = new Agreements();
    @NotAudited
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "answer_id", referencedColumnName = "id")
    private Answer answer;
    @NotAudited
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "rejection_id", referencedColumnName = "id")
    private Rejection rejection;
    @Version
    private Long version;

    protected Petition() {
    }

    public Petition(String title, String description, Category category, Long userId) {
        this.title = new Title(title);
        this.description = new Description(description);
        this.category = category;
        this.userId = userId;
    }

    public void placeTemporary(String tempUrl, Instant at) {
        this.status = TEMPORARY;
        this.tempUrl = tempUrl;
        this.expiredAt = at.plusSeconds(POSTING_PERIOD_BY_SECONDS);
    }

    public void agree(Long userId, String description, Instant at) {
        if (isRejected()) {
            throw new AlreadyRejectedPetitionException();
        }
        if (isExpiredAt(at)) {
            throw new ExpiredPetitionException();
        }
        this.agreements.add(new Agreement(description, userId, this));

        if (agreements.hasSize(REQUIRED_AGREEMENT_FOR_ANSWER)) {
            this.waitingForAnswerAt = at;
        }
    }

    public boolean isAgreedBy(User user) {
        return agreements.isAgreedBy(user.getId());
    }

    public void release(Instant at) {
        if (isExpiredAt(at)) {
            throw new ExpiredPetitionException();
        }
        if (!isTemporary()) {
            throw new NotValidStatusToReleasePetitionException();
        }
        if (agreements.agreeLessThan(REQUIRED_AGREEMENT_FOR_RELEASE)) {
            throw new NotEnoughAgreementException();
        }
        this.status = RELEASED;
    }

    public void cancelRelease() {
        if (!isReleased()) {
            throw new NotReleasedPetitionException();
        }
        this.status = TEMPORARY;
    }

    public void reject(String description, Instant at) {
        if (isExpiredAt(at)) {
            throw new ExpiredPetitionException();
        }
        if (!isTemporary()) {
            throw new NotValidStatusToRejectPetitionException();
        }
        this.rejection = new Rejection(description, this);
        this.status = REJECTED;
    }

    public void updateRejection(String description) {
        if (!isRejected()) {
            throw new NotRejectedPetitionException();
        }
        this.rejection.update(description);
    }

    public void cancelRejection() {
        if (!isRejected()) {
            throw new NotRejectedPetitionException();
        }
        this.rejection = null;
        this.status = TEMPORARY;
    }

    public void answer(String description, String videoUrl, UrlMatcher urlMatcher) {
        if (!isReleased()) {
            throw new NotValidStatusToAnswerPetitionException();
        }
        if (agreements.agreeLessThan(REQUIRED_AGREEMENT_FOR_ANSWER)) {
            throw new NotEnoughAgreementException();
        }
        this.answer = new Answer(description, VideoUrl.of(videoUrl, urlMatcher), this);
        this.status = ANSWERED;
    }

    public void updateAnswer(String description, String videoUrl, UrlMatcher urlMatcher) {
        if (!isAnswered()) {
            throw new NotAnsweredPetitionException();
        }
        this.answer.update(description, videoUrl, urlMatcher);
    }

    public void deleteAnswer() {
        if (!isAnswered()) {
            throw new NotAnsweredPetitionException();
        }
        this.answer = null;
        this.status = RELEASED;
    }

    public void update(String title, String description, Long categoryId) {
        this.title.update(title);
        this.description.update(description);
        this.category = Category.of(categoryId);
    }

    public boolean isTemporary() {
        return status == TEMPORARY;
    }

    public boolean isReleased() {
        return status == RELEASED;
    }

    public boolean isRejected() {
        return status == REJECTED;
    }

    public boolean isAnswered() {
        return status == ANSWERED;
    }

    public boolean isExpiredAt(Instant time) {
        return time.isAfter(expiredAt);
    }

    public String getTitle() {
        return this.title.getTitle();
    }

    public String getDescription() {
        return this.description.getDescription();
    }

    public int getAgreeCount() {
        return agreements.size();
    }
}
