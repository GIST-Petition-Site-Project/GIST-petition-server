package com.example.gistcompetitioncnserver.post;

import com.example.gistcompetitioncnserver.comment.Comment;
import com.example.gistcompetitioncnserver.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

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
    @GeneratedValue
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

    public boolean applyLike(User user) {
        for (LikeToPost like : likes) {
            if (like.isLikedBy(user.getId())) {
                likes.remove(like);
                return false;
            }
        }
        this.likes.add(new LikeToPost(this, user.getId()));
        return true;
    }
}
