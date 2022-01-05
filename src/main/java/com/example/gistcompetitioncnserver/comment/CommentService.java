package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.post.PostRepository;
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
            throw new CustomException("존재하지 않는 Post입니다");
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
            throw new CustomException("댓글 수정 권한이 없습니다");
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
            throw new CustomException("댓글 삭제 권한이 없습니다");
        }
        commentRepository.deleteById(commentId);
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException("존재하지 않는 Comment 입니다"));
    }

    private void checkExistenceByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new CustomException("존재하지 않는 post입니다");
        }
    }
}
