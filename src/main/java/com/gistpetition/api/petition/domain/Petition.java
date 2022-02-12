package com.gistpetition.api.petition.domain;

import com.gistpetition.api.common.persistence.BaseEntity;
import com.gistpetition.api.exception.petition.DuplicatedAgreementException;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Lob
    private String description;
    @Enumerated(EnumType.STRING)
    private Category category;
    private boolean answered;
    private int accepted;
    private Long userId;
    @NotAudited
    @BatchSize(size = 10)
    @OneToMany(mappedBy = "petition", orphanRemoval = true)
    private final List<Agreement> agreements = new ArrayList<>();

    protected Petition() {
    }

    public Petition(String title, String description, Category category, Long userId) {
        this(null, title, description, category, false, 0, userId);
    }

    private Petition(Long id, String title, String description, Category category, boolean answered, int accepted, Long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.answered = answered;
        this.accepted = accepted;
        this.userId = userId;
    }

    public void addAgreement(Agreement newAgreement) {
        for (Agreement agreement : agreements) {
            if (agreement.writtenBy(newAgreement.getUserId())) {
                throw new DuplicatedAgreementException();
            }
        }
        this.agreements.add(newAgreement);
    }

    public boolean isAgreedBy(User user) {
        for (Agreement agreement : agreements) {
            if (agreement.writtenBy(user.getId())) {
                return true;
            }
        }
        return false;
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
}
