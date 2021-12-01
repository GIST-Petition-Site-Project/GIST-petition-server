package com.example.gistcompetitioncnserver.comment;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional
    public Long createComment(Long postId, CommentRequestDto commentRequestDto, Long userId) {
        Comment comment = new Comment(commentRequestDto.getContent(), postId, userId);
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

    public boolean equalUserToComment(Long postCommnetId, Long writerId) {
        return writerId.equals(commentRepository.findUserIdById(postCommnetId));
    }
}
