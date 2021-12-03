package com.example.gistcompetitioncnserver.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class CommentServiceTest {

    private static final Long USER_ID = 1L;
    private static final String CONTENT = "test contents";
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @MockBean
    private CommentValidator commentValidator;
    private Long postId;

    @BeforeEach
    void setUp() {
        postId = postRepository.save(new Post("title", "description", "category", USER_ID)).getId();
    }

    @Test
    void createComment() {
        doNothing().when(commentValidator).validate(any());
        CommentRequest commentRequest = new CommentRequest(CONTENT);

        Long commentId = commentService.createComment(postId, commentRequest, USER_ID);

        Comment comment =
                commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        assertThat(comment.getId()).isEqualTo(commentId);
        assertThat(comment.getPostId()).isEqualTo(postId);
        assertThat(comment.getUserId()).isEqualTo(USER_ID);
        assertThat(comment.getContent()).isEqualTo(CONTENT);
    }

    @Test
    void createNotValidatedComment() {
        doThrow(new CustomException("error")).when(commentValidator).validate(any());
        CommentRequest commentRequest = new CommentRequest(CONTENT);

        assertThatThrownBy(
                () -> commentService.createComment(Long.MAX_VALUE, commentRequest, Long.MAX_VALUE)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void getCommentsByPostId() {
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(CONTENT, postId, USER_ID));
        comments.add(new Comment(CONTENT, postId, USER_ID));
        comments.add(new Comment(CONTENT, postId, USER_ID));
        List<Comment> savedComments = commentRepository.saveAll(comments);

        List<Comment> commentsByPostId = commentService.getCommentsByPostId(postId);

        assertThat(commentsByPostId).hasSize(savedComments.size());
    }

    @Test
    void getCommentsOfNotExistingPost() {
        Long notExistingPostId = Long.MAX_VALUE;
        assertThatThrownBy(
                () -> commentService.getCommentsByPostId(notExistingPostId)
        ).isInstanceOf(CustomException.class);
    }

    @AfterEach
    void tearDown() {
        commentRepository.deleteAllInBatch();
    }
}
