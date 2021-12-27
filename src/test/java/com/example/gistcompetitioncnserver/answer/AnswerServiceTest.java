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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

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
        normalUserId = userRepository.save(new User("userName", "normal@email.com", "password", UserRole.USER)).getId();
        managerUserId = userRepository.save(new User("userName", "manager@email.com", "password", UserRole.MANAGER)).getId();
        adminUserId = userRepository.save(new User("userName", "admin@email.com", "password", UserRole.ADMIN)).getId();
        postId = postRepository.save(new Post("title", "description", "category", normalUserId)).getId();
    }

    @Test
    void createAnswerByManager() {
        AnswerRequestDto answerRequestDto = new AnswerRequestDto(CONTENT);

        Long savedAnswer = answerService.createAnswer(postId, answerRequestDto, managerUserId);

        Answer answer = answerRepository.findById(savedAnswer).orElseThrow(()-> new CustomException("존재하지 않는 answer입니다.;"));
        assertThat(answer.getId()).isEqualTo(savedAnswer);
        assertThat(answer.getContent()).isEqualTo(CONTENT);
        assertThat(answer.getUserId()).isEqualTo(managerUserId);
        assertThat(answer.getPostId()).isEqualTo(postId);

        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException("존재하지 않는 post입니다"));
        assertTrue(post.isAnswered());
    }

    @Test
    void createAnswerByNormalUser() {
        AnswerRequestDto answerRequestDto = new AnswerRequestDto(CONTENT);

        assertThatThrownBy(
                () -> answerService.createAnswer(postId, answerRequestDto, normalUserId)
        ).isInstanceOf(CustomException.class);

        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException("존재하지 않는 post입니다"));
        assertFalse(post.isAnswered());
    }

    @Test
    void createAnswerByNonExistentUser() {
        AnswerRequestDto answerRequestDto = new AnswerRequestDto(CONTENT);

        Long fakeUserId = Long.MAX_VALUE;
        assertThatThrownBy(
                () -> answerService.createAnswer(postId, answerRequestDto, fakeUserId)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void createAnswerByNonExistentPost() {
        AnswerRequestDto answerRequestDto = new AnswerRequestDto(CONTENT);

        Long fakePostId = Long.MAX_VALUE;
        assertThatThrownBy(
                () -> answerService.createAnswer(fakePostId, answerRequestDto, managerUserId)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void retrieveAnswer(){
        Answer answer= new Answer(CONTENT,postId,managerUserId);
        answerRepository.save(answer);

        Answer retrievedAnswer = answerService.retrieveAnswerByPostId(postId);

        assertThat(answer.getId()).isEqualTo(retrievedAnswer.getId());
    }

    @Test
    void retrieveAnswerWithNoAnswer(){
        assertThatThrownBy(
                () -> answerService.retrieveAnswerByPostId(postId)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void retrieveAnswerByNonExistentPost(){
        Answer answer= new Answer(CONTENT,postId,managerUserId);
        answerRepository.save(answer);

        Long fakePostId = Long.MAX_VALUE;
        assertThatThrownBy(
                () -> answerService.retrieveAnswerByPostId(fakePostId)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void updateAnswer() {
        Answer answer = new Answer(CONTENT, postId, managerUserId);
        answerRepository.save(answer);
        String changContent = "change contents";
        AnswerRequestDto changeRequest = new AnswerRequestDto(changContent);

        answerService.updateAnswer(managerUserId, postId, changeRequest);

        Answer updatedAnswer = answerRepository.findByPostId(postId).orElseThrow(() -> new CustomException(""));
        assertThat(answer.getId()).isEqualTo(updatedAnswer.getId());
        assertThat(updatedAnswer.getContent()).isEqualTo(changContent);
    }

    @Test
    void updateAnswerByOwnerButNormalUser() {
        Answer answer = new Answer(CONTENT, postId, managerUserId);
        answerRepository.save(answer);
        String changContent = "change contents";
        AnswerRequestDto changeRequest = new AnswerRequestDto(changContent);

        User user = userRepository.findById(managerUserId).orElseThrow(() -> new CustomException(""));
        user.setUserRole(UserRole.USER);
        userRepository.save(user);

        assertThatThrownBy(
                () -> answerService.updateAnswer(user.getId(), postId, changeRequest)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void updateAnswerByOtherManager() {
        Answer answer = new Answer(CONTENT, postId, managerUserId);
        answerRepository.save(answer);
        String changContent = "change contents";
        AnswerRequestDto changeRequest = new AnswerRequestDto(changContent);

        User otherManager = new User("testUser", "otherManager@email.com", "pw", UserRole.MANAGER);
        userRepository.save(otherManager);

        assertThatThrownBy(
                () -> answerService.updateAnswer(otherManager.getId(), postId, changeRequest)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void updateAnswerWithNonExistentPost() {
        Long fakePostId = Long.MAX_VALUE;
        String changContent = "change contents";
        AnswerRequestDto changeRequest = new AnswerRequestDto(changContent);

        assertThatThrownBy(
                () -> answerService.updateAnswer(managerUserId, fakePostId, changeRequest)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void updateAnswerWithNoAnswer() {
        String changContent = "change contents";
        AnswerRequestDto changeRequest = new AnswerRequestDto(changContent);

        assertThatThrownBy(
                () -> answerService.updateAnswer(managerUserId, postId, changeRequest)
        ).isInstanceOf(CustomException.class);
    }

    @Test
    void deleteAnswerByOwner() {
        Answer answer = new Answer(CONTENT, postId, managerUserId);
        answerRepository.save(answer);

        answerService.deleteAnswer(managerUserId, postId);

        assertFalse(answerRepository.existsById(answer.getId()));
    }

    @Test
    void deleteAnswerByAdmin() {
        Answer answer = new Answer(CONTENT, postId, managerUserId);
        answerRepository.save(answer);

        answerService.deleteAnswer(managerUserId, postId);

        assertFalse(answerRepository.existsById(answer.getId()));
    }

    @Test
    void deleteAnswerByOther() {
        Answer answer = new Answer(CONTENT, postId, managerUserId);
        answerRepository.save(answer);

        User other = userRepository.save(
                new User("other", "otherManager", "pswd", UserRole.MANAGER)
        );

        assertThatThrownBy(
                () ->  answerService.deleteAnswer(other.getId(), postId)
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