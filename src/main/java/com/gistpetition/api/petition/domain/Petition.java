package com.gistpetition.api.petition.domain;

import com.gistpetition.api.common.persistence.BaseEntity;
import com.gistpetition.api.exception.petition.*;
import com.gistpetition.api.user.domain.User;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Audited
@Getter
@Entity
public class Petition extends BaseEntity {
    public static final int REQUIRED_AGREEMENT_FOR_RELEASE = 5;
    public static final int REQUIRED_AGREEMENT_FOR_ANSWER = 20;
    public static final int POSTING_PERIOD_BY_SECONDS = 30 * 24 * 60 * 60;

    private String title;
    @Lob
    private String description;
    @Enumerated(EnumType.STRING)
    private Category category;
    private Boolean answered = false;
    private Boolean released = false;
    private Instant expiredAt;
    private Long userId;
    @Column(unique = true)
    private String tempUrl;
    @NotAudited
    private Integer agreeCount = 0;
    @NotAudited
    @BatchSize(size = 10)
    @OneToMany(mappedBy = "petition", orphanRemoval = true)
    private final List<Agreement> agreements = new ArrayList<>();

    protected Petition() {
    }

    public Petition(String title, String description, Category category, Instant expiredAt, Long userId, String tempUrl) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.expiredAt = expiredAt;
        this.userId = userId;
        this.tempUrl = tempUrl;
    }

    public void addAgreement(Agreement newAgreement, Instant at) {
        if (agreements.contains(newAgreement)) {
            throw new DuplicatedAgreementException();
        }
        if (isExpiredAt(at)) {
            throw new ExpiredPetitionException();
        }
        this.agreements.add(newAgreement);
        this.agreeCount += 1;
    }

    public boolean isAgreedBy(User user) {
        for (Agreement agreement : agreements) {
            if (agreement.writtenBy(user.getId())) {
                return true;
            }
        }
        return false;
    }

    public void release(Instant at) {
        if (isExpiredAt(at)) {
            throw new ExpiredPetitionException();
        }
        if (released) {
            throw new AlreadyReleasedPetitionException();
        }
        if (agreeCount < REQUIRED_AGREEMENT_FOR_RELEASE) {
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

    public void setAnswered(boolean b) {
        this.answered = b;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isReleased() {
        return released;
    }

    public boolean isAnswered() {
        return answered;
    }

    public boolean isExpiredAt(Instant time) {
        return time.isAfter(expiredAt);
    }
}
