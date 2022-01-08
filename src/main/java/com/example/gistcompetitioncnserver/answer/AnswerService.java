package com.example.gistcompetitioncnserver.answer;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.exception.post.DuplicatedAnswerException;
import com.example.gistcompetitioncnserver.exception.post.NoSuchPostException;
import com.example.gistcompetitioncnserver.exception.post.UnAnsweredPostException;
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

    public AnswerService(AnswerRepository answerRepository,
                         PostRepository postRepository,
                         UserRepository userRepository) {
        this.answerRepository = answerRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public Long createAnswer(Long postId, AnswerRequest answerRequest, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(NoSuchPostException::new);
        if (post.isAnswered()) {
            throw new DuplicatedAnswerException();
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
            throw new NoSuchPostException();
        }
    }

    private Answer findAnswerByPostId(Long postId) {
        return answerRepository.findByPostId(postId).orElseThrow(UnAnsweredPostException::new);
    }
}
