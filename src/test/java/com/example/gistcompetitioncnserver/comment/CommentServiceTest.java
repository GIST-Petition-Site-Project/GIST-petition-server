package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostRepository;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRepository;
import com.example.gistcompetitioncnserver.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
class CommentServiceTest {

    private static final String CONTENT = "test contents";
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @MockBean
    private CommentValidator commentValidator;

    private Long userId;
    private Long postId;

    @BeforeEach
    void setUp() {
        userId = userRepository.save(new User("email@email.com", "password", UserRole.USER)).getId();
        postId = postRepository.save(new Post("title", "description", "category", userId)).getId();
    }

    @Test
    void createComment() {
        doNothing().when(commentValidator).validate(any());
        CommentRequest commentRequest = new CommentRequest(CONTENT);

        Long commentId = commentService.createComment(postId, commentRequest, userId);

        Comment comment =
                commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        assertThat(comment.getId()).isEqualTo(commentId);
        assertThat(comment.getPostId()).isEqualTo(postId);
        assertThat(comment.getUserId()).isEqualTo(userId);
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
        comments.add(new Comment(CONTENT, postId, userId));
        comments.add(new Comment(CONTENT, postId, userId));
        comments.add(new Comment(CONTENT, postId, userId));
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

    @Test
    void updateCommentByOwner() {
        String contentToChange = "changed Content";
        Long savedId = commentRepository.save(new Comment(CONTENT, postId, userId)).getId();

        commentService.updateComment(userId, savedId, new CommentRequest(contentToChange));

        Comment comment = commentRepository.findById(savedId).orElseThrow(IllegalArgumentException::new);
        assertThat(comment.getContent()).isEqualTo(contentToChange);
    }

    @ParameterizedTest
    @EnumSource(value = UserRole.class)
    void updateCommentByOther(UserRole userRole) {
        User other = userRepository.save(new User("other@other.com", "password", userRole));
        CommentRequest updateRequest = new CommentRequest("changed Content");
        Long savedId = commentRepository.save(new Comment(CONTENT, postId, userId)).getId();
        assertThatThrownBy(
                () -> commentService.updateComment(other.getId(), savedId, updateRequest)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void deleteByOwner() {
        Comment saved = commentRepository.save(new Comment(CONTENT, postId, userId));

        commentService.deleteComment(userId, saved.getId());

        assertFalse(commentRepository.existsById(saved.getId()));
    }

    @Test
    void deleteByAdmin() {
        User admin = userRepository.save(new User("admin@admin.com", "password", UserRole.ADMIN));
        Comment saved = commentRepository.save(new Comment(CONTENT, postId, userId));

        commentService.deleteComment(admin.getId(), saved.getId());

        assertFalse(commentRepository.existsById(saved.getId()));
    }

    @Test
    void deleteByOther() {
        User other = userRepository.save(new User("other@other.com", "password", UserRole.USER));
        Comment saved = commentRepository.save(new Comment(CONTENT, postId, userId));

        assertThatThrownBy(
                () -> commentService.deleteComment(other.getId(), saved.getId())
        ).isInstanceOf(CustomException.class);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
    }
}
