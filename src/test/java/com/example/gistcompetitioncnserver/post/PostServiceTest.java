package com.example.gistcompetitioncnserver.post;

import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRepository;
import com.example.gistcompetitioncnserver.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles(profiles = "test")
public class PostServiceTest {
    private static final PostRequestDto POST_REQUEST_DTO = new PostRequestDto("title", "description", "category");
    @Autowired
    private PostService postService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private AgreementRepository agreementRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User("userName", "email", "password", UserRole.USER));
    }

    @Test
    void createPost() {
        Post post = postService.createPost(POST_REQUEST_DTO, user.getId());

        assertThat(post.getTitle()).isEqualTo(POST_REQUEST_DTO.getTitle());
        assertThat(post.getDescription()).isEqualTo(POST_REQUEST_DTO.getDescription());
        assertThat(post.getCategory()).isEqualTo(POST_REQUEST_DTO.getCategory());
        assertThat(post.getUserId()).isEqualTo(user.getId());
        assertThat(post.getCreatedAt()).isNotNull();
    }

    @Test
    void agree() {
        Long postId = postService.createPost(POST_REQUEST_DTO, user.getId()).getId();

        Post post = postRepository.findPostByWithEagerMode(postId);
        assertThat(post.getAgreements()).hasSize(0);

        postService.agree(postId, user.getId());
        post = postRepository.findPostByWithEagerMode(postId);
        assertThat(post.getAgreements()).hasSize(1);
    }

    @Test
    void numberOfAgreements() {
        Long postId = postService.createPost(POST_REQUEST_DTO, user.getId()).getId();

        User user2 = userRepository.save(new User("userName", "email", "password", UserRole.USER));
        User user3 = userRepository.save(new User("userName", "email", "password", UserRole.USER));

        assertThat(postService.getNumberOfAgreements(postId)).isEqualTo(0);

        postService.agree(postId, user.getId());
        postService.agree(postId, user2.getId());
        postService.agree(postId, user3.getId());

        assertThat(postService.getNumberOfAgreements(postId)).isEqualTo(3);
    }

    @Test
    void getStateOfAgreement() {
        Long postId = postService.createPost(POST_REQUEST_DTO, user.getId()).getId();

        assertThat(postService.getStateOfAgreement(postId, user.getId())).isFalse();
        postService.agree(postId, user.getId());
        assertThat(postService.getStateOfAgreement(postId, user.getId())).isTrue();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        agreementRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
    }
}
