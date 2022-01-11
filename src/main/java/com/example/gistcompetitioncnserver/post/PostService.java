package com.example.gistcompetitioncnserver.post;


import com.example.gistcompetitioncnserver.exception.post.NoSuchPostException;
import com.example.gistcompetitioncnserver.exception.user.NoSuchUserException;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

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
    public Long getPostCount() {
        return postRepository.count();
    }

    @Transactional(readOnly = true)
    public List<Post> getPostsByCategory(String categoryName) {
        return postRepository.findByCategory(Sort.by(Sort.Direction.DESC, "id"), categoryName);
    }

    @Transactional
    public void updatePostDescription(Long postId, String description) {
        Post post = findPostById(postId);
        post.setDescription(description);
    }

    @Transactional
    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NoSuchPostException();
        }
        postRepository.deleteById(postId);
        eventPublisher.publishEvent(new PostDeleteEvent(postId));
    }

    @Transactional
    public Boolean agree(Long postId, Long userId) {
        Post post = findPostById(postId);
        User user = findUserById(userId);
        return post.applyAgreement(user);
    }

    @Transactional(readOnly = true)
    public int getNumberOfAgreements(Long postId) {
        Post post = findPostById(postId);
        return post.getAgreements().size();
    }

    @Transactional(readOnly = true)
    public Boolean getStateOfAgreement(Long postId, Long userId) {
        Post post = findPostById(postId);
        User user = findUserById(userId);
        return post.isAgreedBy(user);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(NoSuchUserException::new);
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(NoSuchPostException::new);
    }
}
