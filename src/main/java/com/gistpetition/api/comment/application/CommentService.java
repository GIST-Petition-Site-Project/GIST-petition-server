package com.gistpetition.api.comment.application;

import com.gistpetition.api.comment.domain.Comment;
import com.gistpetition.api.comment.domain.CommentRepository;
import com.gistpetition.api.comment.dto.CommentRequest;
import com.gistpetition.api.exception.comment.NoSuchCommentException;
import com.gistpetition.api.exception.petition.NoSuchPetitionException;
import com.gistpetition.api.exception.user.UnAuthorizedUserException;
import com.gistpetition.api.petition.domain.PetitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PetitionRepository petitionRepository;

    public CommentService(CommentRepository commentRepository,
                          PetitionRepository petitionRepository) {
        this.commentRepository = commentRepository;
        this.petitionRepository = petitionRepository;
    }

    @Transactional
    public Long createComment(Long petitionId, CommentRequest commentRequest, Long userId) {
        checkExistenceByPetitionId(petitionId);
        Comment comment = new Comment(commentRequest.getContent(), petitionId, userId);
        return commentRepository.save(comment).getId();
    }


    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPetitionId(Long petitionId) {
        if (!petitionRepository.existsById(petitionId)) {
            throw new NoSuchPetitionException();
        }
        return commentRepository.findByPetitionId(petitionId);
    }

    @Transactional
    public void updateComment(Long commentId, CommentRequest updateRequest) {
        Comment comment = findCommentById(commentId);
        comment.updateContent(updateRequest.getContent());
    }

    @Transactional
    public void updateCommentByOwner(Long updaterId, Long commentId, CommentRequest updateRequest) {
        Comment comment = findCommentById(commentId);
        if (!comment.getUserId().equals(updaterId)) {
            throw new UnAuthorizedUserException();
        }
        comment.updateContent(updateRequest.getContent());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = findCommentById(commentId);
        commentRepository.deleteById(comment.getId());
    }

    @Transactional
    public void deleteCommentByOwner(Long eraserId, Long commentId) {
        Comment comment = findCommentById(commentId);
        if (!comment.getUserId().equals(eraserId)) {
            throw new UnAuthorizedUserException();
        }
        commentRepository.deleteById(commentId);
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(NoSuchCommentException::new);
    }

    private void checkExistenceByPetitionId(Long petitionId) {
        if (!petitionRepository.existsById(petitionId)) {
            throw new NoSuchPetitionException();
        }
    }
}
