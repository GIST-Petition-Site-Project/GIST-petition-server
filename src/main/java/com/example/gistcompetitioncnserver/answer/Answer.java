package com.example.gistcompetitioncnserver.post;

import com.example.gistcompetitioncnserver.comment.Comment;
import com.example.gistcompetitioncnserver.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Post {

    @Id
    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator="native"
    )
    @GenericGenerator(
            name = "native",
            strategy = "native"
    )
    @Column(name = "postId")
    private Long id;

    @OneToMany(mappedBy = "post")
    private final List<Comment> comment = new ArrayList<>();

    private String title;

    private String description;

    private String category;

    private String created;

    private boolean answered;

    private int accepted;

    private Long userId;

//    //foreign key
//    @ManyToOne
//    @JoinColumn(name = "id")
//    private User user;


}
