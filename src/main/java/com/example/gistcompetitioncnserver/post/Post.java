package com.example.gistcompetitioncnserver.post;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    private String writer;

    private String title;

    private Date created;

    private String description;


}
