package com.example.gistcompetitioncnserver.answer;


import com.example.gistcompetitioncnserver.post.Post;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Answer createAnswer(Answer answer, Post post) {
        answer.setCategory(post.getCategory());
        answer.setTitle("RE:" + post.getTitle());
        return answerRepository.save(answer);
    }

    public List<Answer> retrieveAllAnswers() {
        return answerRepository.findAll();
    }

    public List<Answer> retrieveAnswersByUserId(Long user_id) {
        return answerRepository.findByUserId(user_id);
    }

    public Optional<Answer> retrieveAnswer(Long id) {
        return answerRepository.findById(id);
    }

    public Long getPageNumber() {
        return answerRepository.count();
    }

    public List<Answer> getAnswersByCategory(String categoryName) {
        return answerRepository.findByCategory(categoryName);
    }

    @Transactional
    public void deleteAnswer(Long id) {
        answerRepository.deleteById(id);
    }
}
