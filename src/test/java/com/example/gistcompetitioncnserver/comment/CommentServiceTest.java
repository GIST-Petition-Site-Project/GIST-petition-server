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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

    private Long USER_ID;
    private Long MANAGER_ID;
    private Long ADMIN_ID;
    private Long POST_OWNER_ID;
    private Long POST_ID;

    @BeforeEach
    void setUp() {
        USER_ID = userRepository.save(new User("user@email.com", "password", UserRole.USER)).getId();
        MANAGER_ID = userRepository.save(new User("manager@email.com", "password", UserRole.MANAGER)).getId();
        ADMIN_ID = userRepository.save(new User("admin@admin.com", "password", UserRole.ADMIN)).getId();
        POST_OWNER_ID = userRepository.save(new User("postOwner@email.com", "password", UserRole.USER)).getId();
        POST_ID = postRepository.save(new Post("title", "description", "category", POST_OWNER_ID)).getId();
    }

    @Test
    void createComment() {
        CommentRequest commentRequest = new CommentRequest(CONTENT);

        Long commentId = commentService.createComment(POST_ID, commentRequest, USER_ID);

        Comment comment = commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        assertThat(comment.getId()).isEqualTo(commentId);
        assertThat(comment.getPostId()).isEqualTo(POST_ID);
        assertThat(comment.getUserId()).isEqualTo(USER_ID);
        assertThat(comment.getContent()).isEqualTo(CONTENT);
    }

    @Test
    void createFailedIfPostNotExistent() {
        CommentRequest commentRequest = new CommentRequest(CONTENT);

        Long fakePostId = Long.MAX_VALUE;
        assertThatThrownBy(
                () -> commentService.createComment(fakePostId, commentRequest, USER_ID)
        ).isInstanceOf(CustomException.class);
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

    @Test
    void getFailedIfPostNotExistent() {
        Long fakePostId = Long.MAX_VALUE;
        assertThatThrownBy(
                () -> commentService.getCommentsByPostId(fakePostId)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void updateCommentByOwnerUser() {
        String contentToChange = "changed Content";
        Long savedCommentId = commentRepository.save(new Comment(CONTENT, POST_ID, USER_ID)).getId();

        commentService.updateComment(MANAGER_ID, UserRole.MANAGER, savedCommentId, new CommentRequest(contentToChange));

        Comment comment = commentRepository.findById(savedCommentId).orElseThrow(IllegalArgumentException::new);
        assertThat(comment.getContent()).isEqualTo(contentToChange);
    }

    @Test
    void updateCommentByOtherUser() {
        User other = userRepository.save(new User("other@other.com", "password", UserRole.USER));
        String contentToChange = "changed Content";
        CommentRequest updateRequest = new CommentRequest(contentToChange);
        Long savedCommentId = commentRepository.save(new Comment(CONTENT, POST_ID, USER_ID)).getId();

        assertThatThrownBy(
                () -> commentService.updateComment(other.getId(), UserRole.USER, savedCommentId, updateRequest)
        ).isInstanceOf(CustomException.class);
    }
    @Test
    void updateCommentByOtherManger() {
        String contentToChange = "changed Content";
        Long savedId = commentRepository.save(new Comment(CONTENT, POST_ID, USER_ID)).getId();

        CommentRequest commentChangeRequest = new CommentRequest(contentToChange);
        commentService.updateComment(MANAGER_ID, UserRole.MANAGER, savedId, commentChangeRequest);

        Comment comment = commentRepository.findById(savedId).orElseThrow(IllegalArgumentException::new);
        assertThat(comment.getContent()).isEqualTo(contentToChange);
    }


    @Test
    void deleteByOwnerUser() {
        Comment saved = commentRepository.save(new Comment(CONTENT, POST_ID, USER_ID));

        commentService.deleteComment(USER_ID, UserRole.USER, saved.getId());

        assertFalse(commentRepository.existsById(saved.getId()));
    }

    @Test
    void deleteByOtherUser() {
        User other = userRepository.save(new User("other@gist.ac.kr", "password", UserRole.USER));
        Long savedContentId = commentRepository.save(new Comment(CONTENT, POST_ID, USER_ID)).getId();

        assertThatThrownBy(
                () -> commentService.deleteComment(other.getId(), UserRole.USER, savedContentId)
        ).isInstanceOf(CustomException.class);
    }
    @Test
    void deleteByOtherManager() {
        Comment saved = commentRepository.save(new Comment(CONTENT, POST_ID, USER_ID));

        commentService.deleteComment(MANAGER_ID, UserRole.MANAGER, saved.getId());

        assertFalse(commentRepository.existsById(saved.getId()));
    }
    @Test
    void deleteByAdmin() {
        Comment saved = commentRepository.save(new Comment(CONTENT, POST_ID, USER_ID));

        commentService.deleteComment(ADMIN_ID, UserRole.ADMIN, saved.getId());

        assertFalse(commentRepository.existsById(saved.getId()));
    }



    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
    }
}
