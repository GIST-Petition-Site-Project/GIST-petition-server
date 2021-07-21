package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostRepository;
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
@NoArgsConstructor
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

    private Long id;

    private String content;

    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;


}
