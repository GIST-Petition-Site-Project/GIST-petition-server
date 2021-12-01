package com.example.gistcompetitioncnserver.comment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostRepository;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRepository;
import com.example.gistcompetitioncnserver.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommentValidatorTest {

    @Autowired
    private CommentValidator commentValidator;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User("userName", "email", "password", UserRole.USER));
        post = postRepository.save(new Post("title", "description", "category", user.getId()));
    }

    @Test
    void commentWithNotExistingPostId() {
        Comment comment = new Comment("content", Long.MAX_VALUE, user.getId());
        assertThatThrownBy(() -> commentValidator.validate(comment))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void commentWithNotExistingUserId() {
        Comment comment = new Comment("content", post.getId(), Long.MAX_VALUE);
        assertThatThrownBy(() -> commentValidator.validate(comment))
                .isInstanceOf(CustomException.class);
    }
}
