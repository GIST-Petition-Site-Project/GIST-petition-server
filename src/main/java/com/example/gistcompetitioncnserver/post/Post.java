package com.example.gistcompetitioncnserver.post;

import com.example.gistcompetitioncnserver.comment.Comment;
import com.example.gistcompetitioncnserver.like.LikeToPost;
import com.example.gistcompetitioncnserver.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Post {

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

    @JsonManagedReference
    @OneToMany(mappedBy = "post")
    private final List<Comment> comment = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private final List<LikeToPost> likes = new ArrayList<>();


    public Post() {
    }

    //    //foreign key
//    @ManyToOne
//    @JoinColumn(name = "id")
//    private User user;


}
