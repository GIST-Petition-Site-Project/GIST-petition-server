package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.post.PostRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentValidator commentValidator;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          CommentValidator commentValidator) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.commentValidator = commentValidator;
    }

    @Transactional
    public Long createComment(Long postId, CommentRequest commentRequest, Long userId) {
        Comment comment = new Comment(commentRequest.getContent(), postId, userId);
        comment.validate(commentValidator);
        return commentRepository.save(comment).getId();
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new CustomException("존재하지 않는 Post입니다");
        }
        return commentRepository.findByPostId(postId);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public boolean existCommentId(Long commentId) {
        return commentRepository.findById(commentId).isPresent();
    }

    public boolean equalUserToComment(Long commentId, Long writerId) {
        return writerId.equals(commentRepository.findUserIdById(commentId));
    }
}
