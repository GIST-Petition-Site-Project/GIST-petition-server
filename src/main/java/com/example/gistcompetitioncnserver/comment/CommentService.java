package com.example.gistcompetitioncnserver.comment;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;

    public CommentService(CommentRepository commentRepository,
                          CommentValidator commentValidator) {
        this.commentRepository = commentRepository;
        this.commentValidator = commentValidator;
    }

    @Transactional
    public Long createComment(Long postId, CommentRequest commentRequest, Long userId) {
        Comment comment = new Comment(commentRequest.getContent(), postId, userId);
        comment.validate(commentValidator);
        return commentRepository.save(comment).getId();
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<Comment> getCommentsByPostId(Long id) {
        return commentRepository.findByPostId(id);
    }

    public boolean existCommentId(Long commentId) {
        return commentRepository.findById(commentId).isPresent();
    }

    public boolean equalUserToComment(Long commentId, Long writerId) {
        return writerId.equals(commentRepository.findUserIdById(commentId));
    }
}
