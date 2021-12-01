package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.post.Post;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;

@Getter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private Post post;
    private Long userId;
    private LocalDateTime created;

    protected Comment() {
    }

    public Comment(String content, Post post, Long userId) {
        this(null, content, post, userId, LocalDateTime.now());
    }

    private Comment(Long id, String content, Post post, Long userId, LocalDateTime created) {
        this.id = id;
        this.content = content;
        this.post = post;
        this.userId = userId;
        this.created = created;
    }
}
