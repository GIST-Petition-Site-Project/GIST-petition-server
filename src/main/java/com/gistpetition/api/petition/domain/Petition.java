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
import java.util.ArrayList;
import java.util.List;

@Audited
@Getter
@Entity
public class Petition extends BaseEntity {

    public static final int REQUIRED_AGREMMENT = 5;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Lob
    private String description;
    @Enumerated(EnumType.STRING)
    private Category category;
    private Boolean answered = false;
    private Boolean released = false;
    private Long userId;
    @NotAudited
    private Long agreeCount = 0L;
    @NotAudited
    @BatchSize(size = 10)
    @OneToMany(mappedBy = "petition", orphanRemoval = true)
    private final List<Agreement> agreements = new ArrayList<>();

    protected Petition() {
    }

    public Petition(String title, String description, Category category, Long userId) {
        this(null, title, description, category, userId);
    }

    private Petition(Long id, String title, String description, Category category, Long userId) {
        this.id = id;
        this.title = title;
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
        if (agreements.size() < REQUIRED_AGREMMENT) {
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
}
