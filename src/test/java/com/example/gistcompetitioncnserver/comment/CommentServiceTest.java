package com.example.gistcompetitioncnserver.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import com.example.gistcompetitioncnserver.exception.CustomException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @MockBean
    private CommentValidator commentValidator;

    @Test
    void createComment() {
        doNothing().when(commentValidator).validate(any());
        Long userId = 1L;
        Long postId = 1L;
        String content = "test contents";
        CommentRequest commentRequest = new CommentRequest(content);

        Long commentId = commentService.createComment(postId, commentRequest, userId);

        Comment comment = commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        assertThat(comment.getId()).isEqualTo(commentId);
        assertThat(comment.getPostId()).isEqualTo(postId);
        assertThat(comment.getUserId()).isEqualTo(userId);
        assertThat(comment.getContent()).isEqualTo(content);
    }

    @Test
    void createNotValidatedComment() {
        doThrow(new CustomException("error")).when(commentValidator).validate(any());

        String content = "test contents";
        CommentRequest commentRequest = new CommentRequest(content);

        assertThatThrownBy(() -> commentService.createComment(Long.MAX_VALUE, commentRequest, Long.MAX_VALUE))
                .isInstanceOf(CustomException.class);
    }
}
