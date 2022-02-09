package com.gistpetition.api.petition.domain;

import com.gistpetition.api.common.persistence.BaseEntity;
import com.gistpetition.api.exception.petition.DuplicatedAgreementException;
import com.gistpetition.api.user.domain.User;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @BatchSize(size = 10)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "petition_id")
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

    public void applyAgreement(User user, String description) {
        for (Agreement agreement : agreements) {
            if (agreement.isAgreedBy(user.getId())) {
                throw new DuplicatedAgreementException();
            }
        }
        this.agreements.add(new Agreement(user.getId(), description, this));
    }

    public boolean isAgreedBy(User user) {
        for (Agreement agreement : agreements) {
            if (agreement.isAgreedBy(user.getId())) {
                return true;
            }
        }
        return false;
    }

    public void setAnswered(boolean b) {
        this.answered = b;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
