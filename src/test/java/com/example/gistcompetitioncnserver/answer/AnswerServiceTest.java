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

    private static final String CONTENT = "test contents";

    @Autowired
    private AnswerService answerService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private AnswerRepository answerRepository;

    private Long normalUserId;
    private Long managerUserId;
    private Long adminUserId;
    private Long postId;

    @BeforeEach
    void setup() {
        normalUserId = userRepository.save(new User("normal@email.com", "password", UserRole.USER)).getId();
        managerUserId = userRepository.save(new User("manager@email.com", "password", UserRole.MANAGER)).getId();
        adminUserId = userRepository.save(new User("admin@email.com", "password", UserRole.ADMIN)).getId();
        postId = postRepository.save(new Post("title", "description", "category", normalUserId)).getId();
    }

    @Test
    void createAnswerByAdmin() {
        AnswerRequest answerRequest = new AnswerRequest(CONTENT);

        Long savedAnswer = answerService.createAnswer(postId, answerRequest, adminUserId);

        Answer answer = answerRepository.findById(savedAnswer).orElseThrow(() -> new CustomException("존재하지 않는 answer입니다.;"));
        assertThat(answer.getId()).isEqualTo(savedAnswer);
        assertThat(answer.getContent()).isEqualTo(CONTENT);
        assertThat(answer.getUserId()).isEqualTo(adminUserId);
        assertThat(answer.getPostId()).isEqualTo(postId);

        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException("존재하지 않는 post입니다"));
        assertTrue(post.isAnswered());
    }

    @Test
    void createAnswerByManager() {
        AnswerRequest answerRequest = new AnswerRequest(CONTENT);

        Long savedAnswer = answerService.createAnswer(postId, answerRequest, managerUserId);

        Answer answer = answerRepository.findById(savedAnswer).orElseThrow(() -> new CustomException("존재하지 않는 answer입니다.;"));
        assertThat(answer.getId()).isEqualTo(savedAnswer);
        assertThat(answer.getContent()).isEqualTo(CONTENT);
        assertThat(answer.getUserId()).isEqualTo(managerUserId);
        assertThat(answer.getPostId()).isEqualTo(postId);

        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException("존재하지 않는 post입니다"));
        assertTrue(post.isAnswered());
    }

    @Test
    void createAnswerByNormalUser() {
        AnswerRequest answerRequest = new AnswerRequest(CONTENT);

        assertThatThrownBy(
                () -> answerService.createAnswer(postId, answerRequest, normalUserId)
        ).isInstanceOf(CustomException.class);

        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException("존재하지 않는 post입니다"));
        assertFalse(post.isAnswered());
    }

    @Test
    void createAnswerByNonExistentUser() {
        AnswerRequest answerRequest = new AnswerRequest(CONTENT);

        Long fakeUserId = Long.MAX_VALUE;
        assertThatThrownBy(
                () -> answerService.createAnswer(postId, answerRequest, fakeUserId)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void createAnswerToNonExistentPost() {
        AnswerRequest answerRequest = new AnswerRequest(CONTENT);

        Long fakePostId = Long.MAX_VALUE;
        assertThatThrownBy(
                () -> answerService.createAnswer(fakePostId, answerRequest, managerUserId)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void retrieveAnswerByALL() {
        Answer answer = new Answer(CONTENT, postId, managerUserId);
        answerRepository.save(answer);

        Answer retrievedAnswer = answerService.retrieveAnswerByPostId(postId);

        assertThat(answer.getId()).isEqualTo(retrievedAnswer.getId());
    }

    @Test
    void retrieveAnswerWithNoAnswer() {
        assertThatThrownBy(
                () -> answerService.retrieveAnswerByPostId(postId)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void retrieveAnswerFromNonExistentPost() {
        Answer answer = new Answer(CONTENT, postId, managerUserId);
        answerRepository.save(answer);

        Long fakePostId = Long.MAX_VALUE;
        assertThatThrownBy(
                () -> answerService.retrieveAnswerByPostId(fakePostId)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void updateAnswerByAdmin() {
        Answer answer = new Answer(CONTENT, postId, managerUserId);
        answerRepository.save(answer);
        String changContent = "change contents";
        AnswerRequest changeRequest = new AnswerRequest(changContent);

        answerService.updateAnswer(adminUserId, postId, changeRequest);

        Answer updatedAnswer = answerRepository.findByPostId(postId).orElseThrow(() -> new CustomException(""));
        assertThat(answer.getId()).isEqualTo(updatedAnswer.getId());
        assertThat(updatedAnswer.getContent()).isEqualTo(changContent);
    }

    @Test
    void updateAnswerByOwnerManager() {
        Answer answer = new Answer(CONTENT, postId, managerUserId);
        answerRepository.save(answer);
        String changContent = "change contents";
        AnswerRequest changeRequest = new AnswerRequest(changContent);

        answerService.updateAnswer(managerUserId, postId, changeRequest);

        Answer updatedAnswer = answerRepository.findByPostId(postId).orElseThrow(() -> new CustomException(""));
        assertThat(answer.getId()).isEqualTo(updatedAnswer.getId());
        assertThat(updatedAnswer.getContent()).isEqualTo(changContent);
    }

    @Test
    void updateAnswerByOtherManager() {
        Answer answer = new Answer(CONTENT, postId, managerUserId);
        answerRepository.save(answer);
        String changContent = "change contents";
        AnswerRequest changeRequest = new AnswerRequest(changContent);

        User otherManager = new User("otherManager@email.com", "pw", UserRole.MANAGER);
        userRepository.save(otherManager);

        assertThatThrownBy(
                () -> answerService.updateAnswer(otherManager.getId(), postId, changeRequest)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void updateAnswerByOwnerButNormalUser() {
        Answer answer = new Answer(CONTENT, postId, managerUserId);
        answerRepository.save(answer);
        String changContent = "change contents";
        AnswerRequest changeRequest = new AnswerRequest(changContent);

        User user = userRepository.findById(managerUserId).orElseThrow(() -> new CustomException(""));
        user.setUserRole(UserRole.USER);
        userRepository.save(user);

        assertThatThrownBy(
                () -> answerService.updateAnswer(user.getId(), postId, changeRequest)
        ).isInstanceOf(CustomException.class);
    }


    @Test
    void updateAnswerFromNonExistentPost() {
        Long fakePostId = Long.MAX_VALUE;
        String changContent = "change contents";
        AnswerRequest changeRequest = new AnswerRequest(changContent);

        assertThatThrownBy(
                () -> answerService.updateAnswer(managerUserId, fakePostId, changeRequest)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void updateNonExistentAnswer() {
        String changContent = "change contents";
        AnswerRequest changeRequest = new AnswerRequest(changContent);

        assertThatThrownBy(
                () -> answerService.updateAnswer(managerUserId, postId, changeRequest)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void deleteAnswerByOwnerManager() {
        Answer answer = new Answer(CONTENT, postId, managerUserId);
        answerRepository.save(answer);

        answerService.deleteAnswer(managerUserId, postId);

        assertFalse(answerRepository.existsById(answer.getId()));
    }

    @Test
    void deleteAnswerByAdmin() {
        Answer answer = new Answer(CONTENT, postId, managerUserId);
        answerRepository.save(answer);

        answerService.deleteAnswer(adminUserId, postId);

        assertFalse(answerRepository.existsById(answer.getId()));
    }

    @Test
    void deleteAnswerByOtherManager() {
        Answer answer = new Answer(CONTENT, postId, managerUserId);
        answerRepository.save(answer);

        User other = userRepository.save(
                new User("otherManager@email.com", "pswd", UserRole.MANAGER)
        );

        assertThatThrownBy(
                () -> answerService.deleteAnswer(other.getId(), postId)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void deleteAnswerByOwnerButNormalUser() {
        Answer answer = new Answer(CONTENT, postId, managerUserId);
        answerRepository.save(answer);

        User user = userRepository.findById(managerUserId).orElseThrow(() -> new CustomException(""));
        user.setUserRole(UserRole.USER);
        userRepository.save(user);

        assertThatThrownBy(
                () -> answerService.deleteAnswer(user.getId(), postId)
        ).isInstanceOf(CustomException.class);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        answerRepository.deleteAllInBatch();
    }

}
