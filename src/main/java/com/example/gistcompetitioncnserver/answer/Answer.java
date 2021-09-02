package com.example.gistcompetitioncnserver.answer;

import com.example.gistcompetitioncnserver.comment.Comment;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Answer {

    @Id
    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator="native"
    )
    @GenericGenerator(
            name = "native",
            strategy = "native"
    )
    @Column(name = "answerId")
    private Long id;

    private String title;

    private String description;

    private String category;

    private String created;

    private Long userId;

//    //foreign key
//    @ManyToOne
//    @JoinColumn(name = "id")
//    private User user;


}
