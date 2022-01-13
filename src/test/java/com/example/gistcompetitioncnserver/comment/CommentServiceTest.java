package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.ServiceTest;
import com.example.gistcompetitioncnserver.comment.application.CommentService;
import com.example.gistcompetitioncnserver.comment.domain.Comment;
import com.example.gistcompetitioncnserver.comment.domain.CommentRepository;
import com.example.gistcompetitioncnserver.comment.dto.CommentRequest;
import com.example.gistcompetitioncnserver.exception.post.NoSuchPostException;
import com.example.gistcompetitioncnserver.exception.user.UnAuthorizedUserException;
import com.example.gistcompetitioncnserver.post.domain.Post;
import com.example.gistcompetitioncnserver.post.domain.PostRepository;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRepository;
import com.example.gistcompetitioncnserver.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CommentServiceTest extends ServiceTest {

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

    private Post post;
    private User commenter;
    private User otherUser;

    @BeforeEach
    void setUp() {
        User postOwner = userRepository.save(new User("user@gist.ac.kr", "password", UserRole.USER));
        post = postRepository.save(new Post("title", "description", "category", postOwner.getId()));

        commenter = userRepository.save(new User("commenter@gist.ac.kr", "password", UserRole.USER));
        otherUser = userRepository.save(new User("other@gist.ac.kr", "password", UserRole.USER));
    }

    @Test
    void createComment() {
        Long commentId = commentService.createComment(post.getId(), COMMENT_REQUEST, commenter.getId());

        Comment comment = commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        assertThat(comment.getId()).isEqualTo(commentId);
        assertThat(comment.getPostId()).isEqualTo(post.getId());
        assertThat(comment.getUserId()).isEqualTo(commenter.getId());
        assertThat(comment.getContent()).isEqualTo(COMMENT_REQUEST.getContent());
    }

    @Test
    void createFailedIfPostNotExistent() {
        Long notExistingPostId = Long.MAX_VALUE;
        assertThatThrownBy(
                () -> commentService.createComment(notExistingPostId, COMMENT_REQUEST, commenter.getId())
        ).isInstanceOf(NoSuchPostException.class);
    }

    @Test
    void getCommentsByPostId() {
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(CONTENT, post.getId(), commenter.getId()));
        comments.add(new Comment(CONTENT, post.getId(), commenter.getId()));
        comments.add(new Comment(CONTENT, post.getId(), commenter.getId()));
        List<Comment> savedComments = commentRepository.saveAll(comments);

        List<Comment> commentsByPostId = commentService.getCommentsByPostId(post.getId());

        assertThat(commentsByPostId).hasSize(savedComments.size());
    }

    @Test
    void getFailedIfPostNotExistent() {
        Long notExistingPostId = Long.MAX_VALUE;
        assertThatThrownBy(
                () -> commentService.getCommentsByPostId(notExistingPostId)
        ).isInstanceOf(NoSuchPostException.class);
    }

    @Test
    void updateComment() {
        Long savedCommentId = commentRepository.save(new Comment(CONTENT, post.getId(), commenter.getId())).getId();

        commentService.updateComment(savedCommentId, UPDATE_REQUEST);

        Comment comment = commentRepository.findById(savedCommentId).orElseThrow(IllegalArgumentException::new);
        assertThat(comment.getContent()).isEqualTo(UPDATE_REQUEST.getContent());
    }

    @Test
    void updateCommentByOwner() {
        Long savedCommentId = commentRepository.save(new Comment(CONTENT, post.getId(), commenter.getId())).getId();

        commentService.updateCommentByOwner(commenter.getId(), savedCommentId, UPDATE_REQUEST);

        Comment comment = commentRepository.findById(savedCommentId).orElseThrow(IllegalArgumentException::new);
        assertThat(comment.getContent()).isEqualTo(UPDATE_REQUEST.getContent());
    }

    @Test
    void updateCommentByOtherUser() {
        Long savedCommentId = commentRepository.save(new Comment(CONTENT, post.getId(), commenter.getId())).getId();

        assertThatThrownBy(
                () -> commentService.updateCommentByOwner(otherUser.getId(), savedCommentId, UPDATE_REQUEST)
        ).isInstanceOf(UnAuthorizedUserException.class);
    }

    @Test
    void deleteComment() {
        Comment saved = commentRepository.save(new Comment(CONTENT, post.getId(), commenter.getId()));

        commentService.deleteComment(saved.getId());

        assertFalse(commentRepository.existsById(saved.getId()));
    }

    @Test
    void deleteCommentByOwnerUser() {
        Comment saved = commentRepository.save(new Comment(CONTENT, post.getId(), commenter.getId()));

        commentService.deleteCommentByOwner(commenter.getId(), saved.getId());

        assertFalse(commentRepository.existsById(saved.getId()));
    }

    @Test
    void deleteCommentByOtherUser() {
        Long savedContentId = commentRepository.save(new Comment(CONTENT, post.getId(), commenter.getId())).getId();

        assertThatThrownBy(
                () -> commentService.deleteCommentByOwner(otherUser.getId(), savedContentId)
        ).isInstanceOf(UnAuthorizedUserException.class);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
    }
}
