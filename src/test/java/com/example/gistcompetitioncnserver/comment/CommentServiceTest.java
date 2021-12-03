package com.example.gistcompetitioncnserver.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import com.example.gistcompetitioncnserver.exception.CustomException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class CommentServiceTest {

    private static final Long POST_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final String CONTENT = "test contents";
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @MockBean
    private CommentValidator commentValidator;

    @Test
    void createComment() {
        doNothing().when(commentValidator).validate(any());
        CommentRequest commentRequest = new CommentRequest(CONTENT);

        Long commentId = commentService.createComment(POST_ID, commentRequest, USER_ID);

        Comment comment =
                commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        assertThat(comment.getId()).isEqualTo(commentId);
        assertThat(comment.getPostId()).isEqualTo(POST_ID);
        assertThat(comment.getUserId()).isEqualTo(USER_ID);
        assertThat(comment.getContent()).isEqualTo(CONTENT);
    }

    @Test
    void createNotValidatedComment() {
        doThrow(new CustomException("error")).when(commentValidator).validate(any());
        CommentRequest commentRequest = new CommentRequest(CONTENT);

        assertThatThrownBy(
                () -> commentService.createComment(Long.MAX_VALUE, commentRequest, Long.MAX_VALUE))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void getCommentsByPostId() {
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(CONTENT, POST_ID, USER_ID));
        comments.add(new Comment(CONTENT, POST_ID, USER_ID));
        comments.add(new Comment(CONTENT, POST_ID, USER_ID));
        List<Comment> savedComments = commentRepository.saveAll(comments);

        List<Comment> commentsByPostId = commentService.getCommentsByPostId(POST_ID);

        assertThat(commentsByPostId).hasSize(savedComments.size());
    }

    @AfterEach
    void tearDown() {
        commentRepository.deleteAllInBatch();
    }
}
