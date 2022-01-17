package com.gistpetition.api.comment.domain;

import com.gistpetition.api.common.persistence.BaseEntity;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private String content;
    private Long petitionId;
    private Long userId;

    protected Comment() {
    }

    public Comment(String content, Long petitionId, Long userId) {
        this(null, content, petitionId, userId);
    }

    public Comment(Long id, String content, Long petitionId, Long userId) {
        this.id = id;
        this.content = content;
        this.petitionId = petitionId;
        this.userId = userId;
    }

    public void updateContent(String changedContent) {
        this.content = changedContent;
    }
}
