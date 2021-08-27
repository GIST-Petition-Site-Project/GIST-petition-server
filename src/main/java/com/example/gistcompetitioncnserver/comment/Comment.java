package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.post.Post;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class Comment {

    @Id
    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator="native"
    )
    @GenericGenerator(
            name = "native",
            strategy = "native"
    )
    @Column(name = "commentId")
    private Long commentId;

    private String content;

    private String created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="postId")
    @JsonIgnore
    private Post post;

    private Long userId;

//    @Builder
//    public Comment( String content, LocalDateTime created, Post post) {
//        this.content = content;
//        this.created = created;
//        this.post = post;
//    }


}
