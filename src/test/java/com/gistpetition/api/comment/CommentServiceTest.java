package com.gistpetition.api.comment;

import com.gistpetition.api.ServiceTest;
import com.gistpetition.api.comment.application.CommentService;
import com.gistpetition.api.comment.domain.Comment;
import com.gistpetition.api.comment.domain.CommentRepository;
import com.gistpetition.api.comment.dto.CommentRequest;
import com.gistpetition.api.exception.petition.NoSuchPetitionException;
import com.gistpetition.api.exception.user.UnAuthorizedUserException;
import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.Petition;
import com.gistpetition.api.petition.domain.PetitionRepository;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
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
    private PetitionRepository petitionRepository;
    @Autowired
    private UserRepository userRepository;

    private Petition petition;
    private User commenter;
    private User otherUser;

    @BeforeEach
    void setUp() {
        User petitionOwner = userRepository.save(new User("user@gist.ac.kr", "password", UserRole.USER));
        petition = petitionRepository.save(new Petition("title", "description", Category.DORMITORY, petitionOwner.getId()));

        commenter = userRepository.save(new User("commenter@gist.ac.kr", "password", UserRole.USER));
        otherUser = userRepository.save(new User("other@gist.ac.kr", "password", UserRole.USER));
    }

    @Test
    void createComment() {
        Long commentId = commentService.createComment(petition.getId(), COMMENT_REQUEST, commenter.getId());

        Comment comment = commentRepository.findById(commentId).orElseThrow(IllegalArgumentException::new);
        assertThat(comment.getId()).isEqualTo(commentId);
        assertThat(comment.getPetitionId()).isEqualTo(petition.getId());
        assertThat(comment.getUserId()).isEqualTo(commenter.getId());
        assertThat(comment.getContent()).isEqualTo(COMMENT_REQUEST.getContent());
    }

    @Test
    void createFailedIfPetitionNotExistent() {
        Long notExistingPetitionId = Long.MAX_VALUE;
        assertThatThrownBy(
                () -> commentService.createComment(notExistingPetitionId, COMMENT_REQUEST, commenter.getId())
        ).isInstanceOf(NoSuchPetitionException.class);
    }

    @Test
    void getCommentsByPetitionId() {
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(CONTENT, petition.getId(), commenter.getId()));
        comments.add(new Comment(CONTENT, petition.getId(), commenter.getId()));
        comments.add(new Comment(CONTENT, petition.getId(), commenter.getId()));
        List<Comment> savedComments = commentRepository.saveAll(comments);

        List<Comment> commentsByPetitionId = commentService.getCommentsByPetitionId(petition.getId());

        assertThat(commentsByPetitionId).hasSize(savedComments.size());
    }

    @Test
    void getFailedIfPetitionNotExistent() {
        Long notExistingPetitionId = Long.MAX_VALUE;
        assertThatThrownBy(
                () -> commentService.getCommentsByPetitionId(notExistingPetitionId)
        ).isInstanceOf(NoSuchPetitionException.class);
    }

    @Test
    void updateComment() {
        Long savedCommentId = commentRepository.save(new Comment(CONTENT, petition.getId(), commenter.getId())).getId();

        commentService.updateComment(savedCommentId, UPDATE_REQUEST);

        Comment comment = commentRepository.findById(savedCommentId).orElseThrow(IllegalArgumentException::new);
        assertThat(comment.getContent()).isEqualTo(UPDATE_REQUEST.getContent());
    }

    @Test
    void updateCommentByOwner() {
        Long savedCommentId = commentRepository.save(new Comment(CONTENT, petition.getId(), commenter.getId())).getId();

        commentService.updateCommentByOwner(commenter.getId(), savedCommentId, UPDATE_REQUEST);

        Comment comment = commentRepository.findById(savedCommentId).orElseThrow(IllegalArgumentException::new);
        assertThat(comment.getContent()).isEqualTo(UPDATE_REQUEST.getContent());
    }

    @Test
    void updateCommentByOtherUser() {
        Long savedCommentId = commentRepository.save(new Comment(CONTENT, petition.getId(), commenter.getId())).getId();

        assertThatThrownBy(
                () -> commentService.updateCommentByOwner(otherUser.getId(), savedCommentId, UPDATE_REQUEST)
        ).isInstanceOf(UnAuthorizedUserException.class);
    }

    @Test
    void deleteComment() {
        Comment saved = commentRepository.save(new Comment(CONTENT, petition.getId(), commenter.getId()));

        commentService.deleteComment(saved.getId());

        assertFalse(commentRepository.existsById(saved.getId()));
    }

    @Test
    void deleteCommentByOwnerUser() {
        Comment saved = commentRepository.save(new Comment(CONTENT, petition.getId(), commenter.getId()));

        commentService.deleteCommentByOwner(commenter.getId(), saved.getId());

        assertFalse(commentRepository.existsById(saved.getId()));
    }

    @Test
    void deleteCommentByOtherUser() {
        Long savedContentId = commentRepository.save(new Comment(CONTENT, petition.getId(), commenter.getId())).getId();

        assertThatThrownBy(
                () -> commentService.deleteCommentByOwner(otherUser.getId(), savedContentId)
        ).isInstanceOf(UnAuthorizedUserException.class);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        petitionRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
    }
}
