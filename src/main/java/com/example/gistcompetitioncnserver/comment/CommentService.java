package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public Long createComment(Long id, CommentRequestDto commentRequestDto, Long userId) {
        Post post = postRepository.getById(id);

        Comment comment = new Comment(commentRequestDto.getContent(), post, userId);
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

    // Method to check if the real comment writer delete the comment.
    public boolean equalUserToComment(Long postCommnetId, Long writerId) {
        return writerId.equals(commentRepository.findUserIdById(postCommnetId));
    }
}
