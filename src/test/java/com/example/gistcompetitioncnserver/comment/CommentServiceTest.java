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
    public static final CommentRequest COMMENT_REQUEST = new CommentRequest(CONTENT);
    public static final CommentRequest UPDATE_REQUEST = new CommentRequest("changed Content");
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    private User postOwner;
    private User otherUser;
    private Post post;

    @BeforeEach
    void setUp() {
        postOwner = userRepository.save(new User("user@gist.ac.kr", "password", UserRole.USER));
        otherUser = userRepository.save(new User("other@gist.ac.kr", "password", UserRole.USER));
        post = postRepository.save(new Post("title", "description", "category", postOwner.getId()));
    }

    @Test
    void createComment() {
        Long commentId = commentService.createComment(post.getId(), COMMENT_REQUEST, postOwner.getId());

        Comment comment = commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        assertThat(comment.getId()).isEqualTo(commentId);
        assertThat(comment.getPostId()).isEqualTo(post.getId());
        assertThat(comment.getUserId()).isEqualTo(postOwner.getId());
        assertThat(comment.getContent()).isEqualTo(COMMENT_REQUEST.getContent());
    }

    @Test
    void createFailedIfPostNotExistent() {
        Long notExistingPostId = Long.MAX_VALUE;
        assertThatThrownBy(
                () -> commentService.createComment(notExistingPostId, COMMENT_REQUEST, postOwner.getId())
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void getCommentsByPostId() {
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(CONTENT, post.getId(), postOwner.getId()));
        comments.add(new Comment(CONTENT, post.getId(), postOwner.getId()));
        comments.add(new Comment(CONTENT, post.getId(), postOwner.getId()));
        List<Comment> savedComments = commentRepository.saveAll(comments);

        List<Comment> commentsByPostId = commentService.getCommentsByPostId(post.getId());

        assertThat(commentsByPostId).hasSize(savedComments.size());
    }

    @Test
    void getFailedIfPostNotExistent() {
        Long notExistingPostId = Long.MAX_VALUE;
        assertThatThrownBy(
                () -> commentService.getCommentsByPostId(notExistingPostId)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void updateComment() {
        Long savedCommentId = commentRepository.save(new Comment(CONTENT, post.getId(), postOwner.getId())).getId();

        commentService.updateComment(savedCommentId, UPDATE_REQUEST);

        Comment comment = commentRepository.findById(savedCommentId).orElseThrow(IllegalArgumentException::new);
        assertThat(comment.getContent()).isEqualTo(UPDATE_REQUEST.getContent());
    }

    @Test
    void updateCommentByOwner() {
        Long savedCommentId = commentRepository.save(new Comment(CONTENT, post.getId(), postOwner.getId())).getId();

        commentService.updateCommentByOwner(postOwner.getId(), savedCommentId, UPDATE_REQUEST);

        Comment comment = commentRepository.findById(savedCommentId).orElseThrow(IllegalArgumentException::new);
        assertThat(comment.getContent()).isEqualTo(UPDATE_REQUEST.getContent());
    }

    @Test
    void updateCommentByOtherUser() {
        Long savedCommentId = commentRepository.save(new Comment(CONTENT, post.getId(), postOwner.getId())).getId();

        assertThatThrownBy(
                () -> commentService.updateCommentByOwner(otherUser.getId(), savedCommentId, UPDATE_REQUEST)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void deleteComment() {
        Comment saved = commentRepository.save(new Comment(CONTENT, post.getId(), postOwner.getId()));

        commentService.deleteComment(saved.getId());

        assertFalse(commentRepository.existsById(saved.getId()));
    }

    @Test
    void deleteCommentByOwnerUser() {
        Comment saved = commentRepository.save(new Comment(CONTENT, post.getId(), postOwner.getId()));

        commentService.deleteCommentByOwner(postOwner.getId(), saved.getId());

        assertFalse(commentRepository.existsById(saved.getId()));
    }

    @Test
    void deleteCommentByOtherUser() {
        Long savedContentId = commentRepository.save(new Comment(CONTENT, post.getId(), postOwner.getId())).getId();

        assertThatThrownBy(
                () -> commentService.deleteCommentByOwner(otherUser.getId(), savedContentId)
        ).isInstanceOf(CustomException.class);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
    }
}
