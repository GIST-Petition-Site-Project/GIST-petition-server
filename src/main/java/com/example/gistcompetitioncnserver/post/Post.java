package com.example.gistcompetitioncnserver.post;

import com.example.gistcompetitioncnserver.comment.Comment;
import com.example.gistcompetitioncnserver.like.LikeToPost;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@Entity
public class Post {

    @JsonManagedReference
    @OneToMany(mappedBy = "post")
    private final List<Comment> comment = new ArrayList<>();
    @OneToMany(mappedBy = "post")
    private final List<LikeToPost> likes = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postId")
    private Long id;
    private String title;
    private String description;
    private String category;
    private String created;
    private boolean answered;
    private int accepted;
    private Long userId;

    protected Post() {
    }

    public Post(Long id, String title, String description, String category, String created, boolean answered,
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
}
