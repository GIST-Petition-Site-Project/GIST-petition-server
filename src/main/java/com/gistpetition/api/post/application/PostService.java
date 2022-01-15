package com.gistpetition.api.post.application;


import com.gistpetition.api.exception.post.NoSuchPostException;
import com.gistpetition.api.exception.user.NoSuchUserException;
import com.gistpetition.api.post.domain.Post;
import com.gistpetition.api.post.domain.Category;
import com.gistpetition.api.post.domain.PostRepository;
import com.gistpetition.api.post.dto.PostRequest;
import com.gistpetition.api.post.dto.PostResponse;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                        Category.getById(postRequest.getCategoryId()),
                        userId)
        ).getId();
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> retrievePost(Pageable pageable) {
        return PostResponse.pageOf(postRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> retrievePostByCategoryId(Long categoryId, Pageable pageable) {
        return PostResponse.pageOf(postRepository.findByCategory(Category.getById(categoryId), pageable));
    }
    @Transactional(readOnly = true)
    public Page<PostResponse> retrievePostByKeyword(String keyword, Pageable pageable) {
        return PostResponse.pageOf(postRepository.findByTitleContains(keyword, pageable));
    }

    //Todo 이건 어디에 쓰이는거죠? 응?
    @Transactional(readOnly = true)
    public List<Post> retrievePostsByUserId(Long user_id) {
        return postRepository.findByUserId(Sort.by(Sort.Direction.DESC, "id"), user_id);
    }

    @Transactional(readOnly = true)
    public PostResponse retrievePostById(Long postId) {
        return PostResponse.of(findPostById(postId));
    }

    @Transactional(readOnly = true)
    public Long getPostCount() {
        return postRepository.count();
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
