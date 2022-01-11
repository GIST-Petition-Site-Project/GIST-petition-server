package com.example.gistcompetitioncnserver.answer;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostRepository;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRepository;
import com.example.gistcompetitioncnserver.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AnswerServiceTest {

    public static final String ANSWER_CONTENT = "test contents";
    public static final AnswerRequest ANSWER_REQUEST = new AnswerRequest("test contents");
    public static final AnswerRequest UPDATE_REQUEST = new AnswerRequest("change contents");

    @Autowired
    private AnswerService answerService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private AnswerRepository answerRepository;

    private User manager;
    private Post savedPost;

    @BeforeEach
    void setup() {
        User user = userRepository.save(new User("normal@email.com", "password", UserRole.USER));
        manager = userRepository.save(new User("manager@email.com", "password", UserRole.MANAGER));
        savedPost = postRepository.save(new Post("title", "description", "category", user.getId()));
    }

    @Test
    void createAnswerByManager() {
        Long savedAnswer = answerService.createAnswer(savedPost.getId(), ANSWER_REQUEST, manager.getId());

        Answer answer = answerRepository.findById(savedAnswer).orElseThrow(() -> new CustomException("존재하지 않는 answer입니다.;"));
        assertThat(answer.getId()).isEqualTo(savedAnswer);
        assertThat(answer.getContent()).isEqualTo(ANSWER_REQUEST.getContent());
        assertThat(answer.getUserId()).isEqualTo(manager.getId());
        assertThat(answer.getPostId()).isEqualTo(savedPost.getId());

        Post answeredPost = postRepository.findById(savedPost.getId()).orElseThrow(() -> new CustomException("존재하지 않는 post입니다"));
        assertTrue(answeredPost.isAnswered());
    }

    @Test
    void createAnswerToNonExistingPost() {
        Long fakePostId = Long.MAX_VALUE;
        assertThatThrownBy(
                () -> answerService.createAnswer(fakePostId, ANSWER_REQUEST, manager.getId())
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void retrieveAnswer() {
        Answer saved = answerRepository.save(new Answer(ANSWER_CONTENT, savedPost.getId(), manager.getId()));

        Answer retrievedAnswer = answerService.retrieveAnswerByPostId(savedPost.getId());

        assertThat(saved.getId()).isEqualTo(retrievedAnswer.getId());
    }

    @Test
    void retrieveAnswerFromNotExistingPost() {
        assertThatThrownBy(
                () -> answerService.retrieveAnswerByPostId(savedPost.getId())
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void retrieveAnswerFromNonExistentPost() {
        Long notExistingPostId = Long.MAX_VALUE;

        assertThatThrownBy(
                () -> answerService.retrieveAnswerByPostId(notExistingPostId)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void updateAnswer() {
        Answer answer = answerRepository.save(new Answer(ANSWER_CONTENT, savedPost.getId(), manager.getId()));

        answerService.updateAnswer(savedPost.getId(), UPDATE_REQUEST);

        Answer updatedAnswer = answerRepository.findByPostId(savedPost.getId()).orElseThrow(() -> new CustomException(""));

        assertThat(answer.getId()).isEqualTo(updatedAnswer.getId());
        assertThat(updatedAnswer.getContent()).isEqualTo(UPDATE_REQUEST.getContent());
    }

    @Test
    void updateAnswerFromNonExistingPost() {
        Long notExistingPostId = Long.MAX_VALUE;

        assertThatThrownBy(
                () -> answerService.updateAnswer(notExistingPostId, UPDATE_REQUEST)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void updateAnswerFromNotAnsweredPost() {
        assertThatThrownBy(
                () -> answerService.updateAnswer(savedPost.getId(), UPDATE_REQUEST)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void deleteAnswer() {
        Answer answer = answerRepository.save(new Answer(ANSWER_CONTENT, savedPost.getId(), manager.getId()));

        answerService.deleteAnswer(savedPost.getId());

        Post post = postRepository.findById(savedPost.getId()).orElseThrow(IllegalArgumentException::new);
        assertFalse(post.isAnswered());
        assertFalse(answerRepository.existsById(answer.getId()));
    }

    @Test
    void deleteAnswerFromNonExistingPost() {
        Long notExistingPostId = Long.MAX_VALUE;

        assertThatThrownBy(
                () -> answerService.deleteAnswer(notExistingPostId)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void deleteAnswerFromNotAnsweredPost() {
        assertThatThrownBy(
                () -> answerService.deleteAnswer(savedPost.getId())
        ).isInstanceOf(CustomException.class);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        answerRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
    }
}
