package com.example.gistcompetitioncnserver.post;

import com.example.gistcompetitioncnserver.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Post {

    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String description;
    private String category;
    private String created;
    private boolean answered;
    private int accepted;
    private Long userId;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "post_id")
    private final List<Agreement> agreements = new ArrayList<>();

    protected Post() {
    }

    public Post(String title, String description, String category, Long userId) {
        this(null, title, description, category,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                false, 0, userId);
    }

    private Post(Long id, String title, String description, String category, String created, boolean answered,
                 int accepted,
                 Long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.created = created;
        this.answered = answered;
        this.accepted = accepted;
        this.userId = userId;
    }

    public boolean applyAgreement(User user) {
        for (Agreement agreement : agreements) {
            if (agreement.isAgreedBy(user.getId())) {
                agreements.remove(agreement);
                return false;
            }
        }
        this.agreements.add(new Agreement(user.getId()));
        return true;
    }
}
