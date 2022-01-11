package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.exception.comment.NoSuchCommentException;
import com.example.gistcompetitioncnserver.exception.post.NoSuchPostException;
import com.example.gistcompetitioncnserver.exception.user.UnAuthorizedUserException;
import com.example.gistcompetitioncnserver.post.PostRepository;
import com.example.gistcompetitioncnserver.user.UserRole;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public Long createComment(Long postId, CommentRequest commentRequest, Long userId) {
        checkExistenceByPostId(postId);
        Comment comment = new Comment(commentRequest.getContent(), postId, userId);
        return commentRepository.save(comment).getId();
    }


    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NoSuchPostException();
        }
        return commentRepository.findByPostId(postId);
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

    private void checkExistenceByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NoSuchPostException();
        }
    }
}
