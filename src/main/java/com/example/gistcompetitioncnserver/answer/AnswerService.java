package com.example.gistcompetitioncnserver.answer;

import com.example.gistcompetitioncnserver.exception.post.DuplicatedAnswerException;
import com.example.gistcompetitioncnserver.exception.post.NoSuchPostException;
import com.example.gistcompetitioncnserver.exception.post.UnAnsweredPostException;
import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final PostRepository postRepository;

    public AnswerService(AnswerRepository answerRepository,
                         PostRepository postRepository) {
        this.answerRepository = answerRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public Long createAnswer(Long postId, AnswerRequest answerRequest, Long userId) {
        Post post = findPostBy(postId);
        if (post.isAnswered()) {
            throw new DuplicatedAnswerException();
        }
        Answer answer = new Answer(answerRequest.getContent(), postId, userId);
        post.setAnswered(true);
        return answerRepository.save(answer).getId();
    }

    @Transactional(readOnly = true)
    public Answer retrieveAnswerByPostId(Long postId) {
        checkExistenceOfPost(postId);
        return findAnswerByPostId(postId);
    }

    @Transactional(readOnly = true)
    public Long getNumberOfAnswers() {
        return answerRepository.count();
    }

    @Transactional
    public void updateAnswer(Long postId, AnswerRequest changeRequest) {
        checkExistenceOfPost(postId);
        Answer answer = findAnswerByPostId(postId);
        answer.updateContent(changeRequest.getContent());
    }

    @Transactional
    public void deleteAnswer(Long postId) {
        Post post = findPostBy(postId);
        checkExistenceOfAnswerOf(postId);
        answerRepository.deleteByPostId(postId);
        post.setAnswered(false);
    }

    private Post findPostBy(Long postId) {
        return postRepository.findById(postId).orElseThrow(NoSuchPostException::new);
    }

    private void checkExistenceOfPost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NoSuchPostException();
        }
    }

    private void checkExistenceOfAnswerOf(Long postId) {
        if (!answerRepository.existsByPostId(postId)) {
            throw new UnAnsweredPostException();
        }
    }

    private Answer findAnswerByPostId(Long postId) {
        return answerRepository.findByPostId(postId).orElseThrow(UnAnsweredPostException::new);
    }
}
