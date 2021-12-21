package com.example.gistcompetitioncnserver.post;

import com.example.gistcompetitioncnserver.common.BaseEntity;
import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.exception.ErrorCase;
import com.example.gistcompetitioncnserver.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Post extends BaseEntity {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "post_id")
    private final List<Agreement> agreements = new ArrayList<>();
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String description;
    private String category;
    private boolean answered;
    private int accepted;
    private Long userId;

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
                throw new CustomException(ErrorCase.INVALID_AGREEMENT);
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

}
