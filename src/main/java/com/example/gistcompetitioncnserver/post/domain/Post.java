package com.example.gistcompetitioncnserver.post.domain;

import com.example.gistcompetitioncnserver.common.persistence.BaseEntity;
import com.example.gistcompetitioncnserver.exception.post.DuplicatedAgreementException;
import com.example.gistcompetitioncnserver.user.domain.User;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Lob
    private String description;
    private String category;
    private boolean answered;
    private int accepted;
    private Long userId;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "post_id")
    private final List<Agreement> agreements = new ArrayList<>();

    protected Post() {
    }

    public Post(String title, String description, String category, Long userId) {
        this(null, title, description, category, false, 0, userId);
    }

    private Post(Long id, String title, String description, String category, boolean answered, int accepted, Long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.answered = answered;
        this.accepted = accepted;
        this.userId = userId;
    }

    public boolean applyAgreement(User user) {
        for (Agreement agreement : agreements) {
            if (agreement.isAgreedBy(user.getId())) {
                throw new DuplicatedAgreementException();
            }
        }
        this.agreements.add(new Agreement(user.getId()));
        return true;
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
