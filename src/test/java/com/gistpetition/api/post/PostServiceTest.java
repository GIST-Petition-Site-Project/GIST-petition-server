package com.gistpetition.api.post;

import com.gistpetition.api.ServiceTest;
import com.gistpetition.api.exception.post.NoSuchPostException;
import com.gistpetition.api.post.application.PostService;
import com.gistpetition.api.post.domain.*;
import com.gistpetition.api.post.dto.PostRequest;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostServiceTest extends ServiceTest {
    private static final PostRequest POST_REQUEST_DTO = new PostRequest("title", "description", 1L);
    @Autowired
    private PostService postService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private AgreementRepository agreementRepository;

    private User postOwner;

    @BeforeEach
    void setUp() {
        postOwner = userRepository.save(new User("email@email.com", "password", UserRole.USER));
    }

    @Test
    void createPost() {
        Long postId = postService.createPost(POST_REQUEST_DTO, postOwner.getId());
        Post post = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);

        assertThat(post.getTitle()).isEqualTo(POST_REQUEST_DTO.getTitle());
        assertThat(post.getDescription()).isEqualTo(POST_REQUEST_DTO.getDescription());
        assertThat(post.getCategory().getId()).isEqualTo(POST_REQUEST_DTO.getCategoryId());
        assertThat(post.getUserId()).isEqualTo(postOwner.getId());
        Assertions.assertThat(post.getCreatedAt()).isNotNull();
    }

    @Test
    void updatePost() {
        Long postId = postService.createPost(POST_REQUEST_DTO, postOwner.getId());
        Post post = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);

        LocalDateTime initialTime = post.getUpdatedAt();

        postService.updatePostDescription(post.getId(), "updated");

        Post updatedPost = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);
        LocalDateTime updatedTime = updatedPost.getUpdatedAt();
        assertTrue(updatedTime.isAfter(initialTime));
    }

    @Test
    void updatePostByNonExistentPostId() {
        Long postId = postService.createPost(POST_REQUEST_DTO, postOwner.getId());
        Post post = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);

        LocalDateTime initialTime = post.getUpdatedAt();

        assertThatThrownBy(() -> postService.updatePostDescription(Long.MAX_VALUE, "updated")).isInstanceOf(NoSuchPostException.class);

        Post updatedPost = postRepository.findById(postId).orElseThrow(IllegalArgumentException::new);
        LocalDateTime updatedTime = updatedPost.getUpdatedAt();
        assertTrue(updatedTime.isEqual(initialTime));
    }

    @Test
    void agree() {
        Long postId = postService.createPost(POST_REQUEST_DTO, postOwner.getId());

        Post post = postRepository.findPostByWithEagerMode(postId);
        Assertions.assertThat(post.getAgreements()).hasSize(0);

        postService.agree(postId, postOwner.getId());
        post = postRepository.findPostByWithEagerMode(postId);
        Assertions.assertThat(post.getAgreements()).hasSize(1);
    }

    @Test
    void numberOfAgreements() {
        Long postId = postService.createPost(POST_REQUEST_DTO, postOwner.getId());

        User user = userRepository.save(new User("email@email.com", "password", UserRole.USER));
        User user3 = userRepository.save(new User("email3@email.com", "password", UserRole.USER));

        assertThat(postService.getNumberOfAgreements(postId)).isEqualTo(0);

        postService.agree(postId, postOwner.getId());
        postService.agree(postId, user.getId());
        postService.agree(postId, user3.getId());

        assertThat(postService.getNumberOfAgreements(postId)).isEqualTo(3);
    }

    @Test
    void getStateOfAgreement() {
        Long postId = postService.createPost(POST_REQUEST_DTO, postOwner.getId());
        assertThat(postService.getStateOfAgreement(postId, postOwner.getId())).isFalse();

        postService.agree(postId, postOwner.getId());

        assertThat(postService.getStateOfAgreement(postId, postOwner.getId())).isTrue();
        Agreement agreement = agreementRepository.findByUserId(postOwner.getId()).orElseThrow(IllegalArgumentException::new);
        Assertions.assertThat(agreement.getCreatedAt()).isNotNull();
    }

    @Test
    void deletePost() {
        Post post = postRepository.save(new Post("title", "description", Category.DORMITORY, postOwner.getId()));
        postService.deletePost(post.getId());
        assertFalse(postRepository.existsById(post.getId()));
    }

    @Test
    void deletePostByNonExistentPostId() {
        assertThatThrownBy(
                () -> postService.deletePost(Long.MAX_VALUE)
        ).isInstanceOf(NoSuchPostException.class);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        agreementRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
    }
}
