package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.jni.Local;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
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
