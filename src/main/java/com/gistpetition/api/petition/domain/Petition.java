package com.gistpetition.api.petition.domain;

import com.gistpetition.api.common.persistence.BaseEntity;
import com.gistpetition.api.exception.petition.AlreadyReleasedPetitionException;
import com.gistpetition.api.exception.petition.NotEnoughAgreementException;
import com.gistpetition.api.user.domain.User;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Audited
@Getter
@Entity
public class Petition extends BaseEntity {


    public static final int REQUIRED_AGREEMENT_FOR_RELEASE = 5;
    public static final int REQUIRED_AGREEMENT_FOR_ANSWER = 20;
    public static final int POSTING_PERIOD = 30;
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(unique = true)
    private String tempUrl;
    @Lob
    private String description;
    @Enumerated(EnumType.STRING)
    private Category category;
    private Boolean answered = false;
    private Boolean released = false;
    private Long userId;
    @NotAudited
    private Integer agreeCount = 0;
    @NotAudited
    @BatchSize(size = 10)
    @OneToMany(mappedBy = "petition", orphanRemoval = true)
    private final List<Agreement> agreements = new ArrayList<>();

    protected Petition() {
    }

    public Petition(String title, String description, Category category, Long userId, String tempUrl) {
        this(null, title, tempUrl, description, category, userId);
    }

    private Petition(Long id, String title, String tempUrl, String description, Category category, Long userId) {
        this.id = id;
        this.title = title;
        this.tempUrl = tempUrl;
        this.description = description;
        this.category = category;
        this.userId = userId;
    }

    public void addAgreement(Agreement newAgreement) {
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

    public void release() {
        if (released) {
            throw new AlreadyReleasedPetitionException();
        }
        if (agreeCount < REQUIRED_AGREEMENT_FOR_RELEASE) {
            throw new NotEnoughAgreementException();
        }
        this.released = true;
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

    public boolean isExpiredAt(LocalDateTime time) {
        LocalDateTime expirationDate = this.createdAt.plusDays(POSTING_PERIOD);
        return expirationDate.isBefore(time);
    }
}
