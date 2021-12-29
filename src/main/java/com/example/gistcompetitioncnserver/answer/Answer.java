package com.example.gistcompetitioncnserver.answer;

import com.example.gistcompetitioncnserver.common.BaseEntity;
import lombok.Getter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
public class Answer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Long postId;
    private Long userId;

    protected Answer() {
    }
  
    public Answer(String content, Long postId, Long userId) {
        this(null, content, postId, userId);
    }
    public Answer(Long id,String content, Long postId, Long userId) {
        this.id = id;
        this.content = content;
        this.postId = postId;
        this.userId = userId;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
