package com.example.gistcompetitioncnserver.post;


import com.example.gistcompetitioncnserver.comment.CommentRepository;
import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.exception.ErrorCase;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Long createPost(PostRequest postRequest, Long userId) {
        return postRepository.save(
                new Post(postRequest.getTitle(),
                        postRequest.getDescription(),
                        postRequest.getCategory(),
                        userId)
        ).getId();
    }

    @Transactional(readOnly = true)
    public List<Post> retrieveAllPost() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Transactional(readOnly = true)
    public List<Post> retrievePostsByUserId(Long user_id) {
        return postRepository.findByUserId(Sort.by(Sort.Direction.DESC, "id"), user_id);
    }

    @Transactional(readOnly = true)
    public Post retrievePost(Long postId) {
        return findPostById(postId);
    }

    @Transactional(readOnly = true)
    public Long getPageNumber() {
        return postRepository.count();
    }

    @Transactional(readOnly = true)
    public List<Post> getPostsByCategory(String categoryName) {
        return postRepository.findByCategory(Sort.by(Sort.Direction.DESC, "id"), categoryName);
    }

    @Transactional
    public void updatePostDescription(Long updaterId, Long postId, String description) {
        Post post = findPostById(postId);
        if (!canUpdate(findUserById(updaterId))) {
            throw new CustomException("청원을 수정할 권한이 없습니다.");
        }
        post.setDescription(description);
    }

    private boolean canUpdate(User user) {
        return user.isAdmin() || user.isManager();
    }

    @Transactional
    public void deletePost(Long eraserId, Long postId) {
        if (!canDelete(findUserById(eraserId))) {
            throw new CustomException("청원을 삭제할 권한이 없습니다.");
        }
        commentRepository.deleteByPostId(postId);
        postRepository.deleteById(postId);
    }

    private boolean canDelete(User user) {
        return user.isAdmin() || user.isManager();
    }

    @Transactional
    public Boolean agree(Long postId, Long userId) {
        Post post = findPostById(postId);
        User user = findUserById(userId);
        return post.applyAgreement(user);
    }

    @Transactional(readOnly = true)
    public int getNumberOfAgreements(Long id) {
        Post post = findPostById(id);
        return post.getAgreements().size();
    }

    @Transactional(readOnly = true)
    public Boolean getStateOfAgreement(Long postId, Long userId) {
        Post post = findPostById(postId);
        User user = findUserById(userId);
        return post.isAgreedBy(user);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCase.NO_SUCH_USER_ERROR));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCase.NO_SUCH_POST_ERROR));
    }
}
