package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.post.PostRepository;
import com.example.gistcompetitioncnserver.user.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class CommentValidator {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentValidator(PostRepository postRepository,
                            UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public void validate(Comment comment) {
        if (!postRepository.existsById(comment.getPostId())) {
            throw new CustomException("존재하지 않은 Post입니다");
        }

        if (!userRepository.existsById(comment.getUserId())) {
            throw new CustomException("존재하지 않은 User입니다");
        }
    }
}
