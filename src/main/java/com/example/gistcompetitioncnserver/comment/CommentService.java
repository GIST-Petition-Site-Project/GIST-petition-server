package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.post.PostRepository;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentValidator commentValidator;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository,
                          CommentValidator commentValidator) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
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

    @Transactional
    public void updateComment(Long updaterId, Long commentId, CommentRequest changeRequest) {
        Comment comment = findCommentById(commentId);
        User user = findUserById(updaterId);
        if (!canUpdate(user, comment)) {
            throw new CustomException("지울 수 있는 권한이 없습니다");
        }
        comment.updateContent(changeRequest.getContent());
    }

    private boolean canUpdate(User user, Comment comment) {
        Long commentOwnerId = comment.getUserId();
        return commentOwnerId.equals(user.getId());
    }

    @Transactional
    public void deleteComment(Long eraserId, Long commentId) {
        Comment comment = findCommentById(commentId);
        User user = findUserById(eraserId);
        if (!canDelete(user, comment)) {
            throw new CustomException("지울 수 있는 권한이 없습니다");
        }
        commentRepository.deleteById(commentId);
    }

    private boolean canDelete(User user, Comment comment) {
        Long commentOwnerId = comment.getUserId();
        return user.isAdmin() || commentOwnerId.equals(user.getId());
    }

    private User findUserById(Long updaterId) {
        return userRepository.findById(updaterId).orElseThrow(() -> new CustomException("존재하지 않는 User 입니다"));
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException("존재하지 않는 Comment 입니다"));
    }
}
