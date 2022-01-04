package com.example.gistcompetitioncnserver.answer;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostRepository;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRepository;
import com.example.gistcompetitioncnserver.user.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public AnswerService(AnswerRepository answerRepository,
                         PostRepository postRepository,
                         UserRepository userRepository) {
        this.answerRepository = answerRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Long createAnswer(Long postId, AnswerRequest answerRequest, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException("존재하지 않는 post입니다"));
        if (post.isAnswered()) {
            throw new CustomException("이미 답변이 된 post입니다.");
        }

        Answer answer = new Answer(answerRequest.getContent(), postId, userId);
        post.setAnswered(true);
        return answerRepository.save(answer).getId();
    }

    @Transactional(readOnly = true)
    public Answer retrieveAnswerByPostId(Long postId) {
        checkExistenceByPostId(postId);
        return findAnswerByPostId(postId);
    }

    @Transactional(readOnly = true)
    public Long getNumberOfAnswers() {
        return answerRepository.count();
    }

    @Transactional
    public void updateAnswer( Long postId, AnswerRequest changeRequest) {
        checkExistenceByPostId(postId);
        Answer answer = findAnswerByPostId(postId);
        answer.updateContent(changeRequest.getContent());
    }

    @Transactional
    public void deleteAnswer(Long postId) {
        checkExistenceByPostId(postId);
        answerRepository.deleteByPostId(postId);
    }


    private void checkExistenceByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new CustomException("존재하지 않는 post입니다");
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomException("존재하지 않는 user입니다"));
    }

    private Answer findAnswerByPostId(Long postId) {
        return answerRepository.findByPostId(postId).orElseThrow(
                () -> new CustomException("해당 post에는 답변이 존재하지 않습니다.")
        );
    }
}
