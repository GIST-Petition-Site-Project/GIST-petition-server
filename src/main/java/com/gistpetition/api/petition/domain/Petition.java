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
import java.util.Objects;

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
    private Boolean released = false;
    private Instant expiredAt;
    private Long userId;
    @Column(unique = true)
    private String tempUrl;
    @Embedded
    @OptimisticLock(excluded = true)
    private final Agreements agreements = new Agreements();
    @NotAudited
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "answer_id", referencedColumnName = "id")
    private Answer answer;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "rejection_id", referencedColumnName = "id")
    private Rejection rejection;
    @Version
    private Long version;

    protected Petition() {
    }

    public Petition(String title, String description, Category category, Instant expiredAt, Long userId, String tempUrl) {
        this.title = new Title(title);
        this.description = new Description(description);
        this.category = category;
        this.expiredAt = expiredAt;
        this.userId = userId;
        this.tempUrl = tempUrl;
    }

    public void agree(Long userId, String description, Instant at) {
        if (isRejected()) {
            throw new AlreadyRejectedPetitionException();
        }
        if (isExpiredAt(at)) {
            throw new ExpiredPetitionException();
        }
        this.agreements.add(new Agreement(description, userId, this));
    }

    public boolean isAgreedBy(User user) {
        return agreements.isAgreedBy(user.getId());
    }

    public void reject(String description, Instant at) {
        if (isExpiredAt(at)) {
            throw new ExpiredPetitionException();
        }
        if (isRejected()) {
            throw new AlreadyRejectedPetitionException();
        }
        if (isAnswered()) {
            throw new AlreadyAnsweredPetitionException();
        }
        if (!released) {
            release(at);
        }
        this.rejection = new Rejection(description, this);
    }

    public void release(Instant at) {
        if (isExpiredAt(at)) {
            throw new ExpiredPetitionException();
        }
        if (released) {
            throw new AlreadyReleasedPetitionException();
        }
        if (agreements.agreeLessThan(REQUIRED_AGREEMENT_FOR_RELEASE)) {
            throw new NotEnoughAgreementException();
        }
        this.released = true;
    }

    public void cancelRelease() {
        if (!released) {
            throw new NotReleasedPetitionException();
        }
        this.released = false;
    }

    public void answer(String description, String videoUrl, UrlMatcher urlMatcher) {
        if (isAnswered()) {
            throw new AlreadyAnsweredPetitionException();
        }
        if (!released) {
            throw new NotReleasedPetitionException();
        }
        if (isRejected()) {
            throw new AlreadyRejectedPetitionException();
        }
        if (agreements.agreeLessThan(REQUIRED_AGREEMENT_FOR_ANSWER)) {
            throw new NotEnoughAgreementException();
        }
        this.answer = new Answer(description, VideoUrl.of(videoUrl, urlMatcher), this);
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
    }

    public void update(String title, String description, Long categoryId) {
        this.title.update(title);
        this.description.update(description);
        this.category = Category.of(categoryId);
    }

    public boolean isReleased() {
        return released;
    }

    public boolean isRejected() {
        return !Objects.isNull(rejection);
    }

    public boolean isAnswered() {
        return !Objects.isNull(answer);
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
