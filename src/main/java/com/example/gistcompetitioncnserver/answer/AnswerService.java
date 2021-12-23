package com.example.gistcompetitioncnserver.answer;


import com.example.gistcompetitioncnserver.post.Post;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;


    @Transactional
    public Long createAnswer(Long postId, AnswerRequestDto answerRequestDto, Long userId) {
        Answer answer = new Answer(answerRequestDto, postId, userId);
        // TODO answer 검증하기
        return answerRepository.save(answer).getId();
    }

    public List<Answer> retrieveAllAnswers(){
        return answerRepository.findAll();
    }

    public List<Answer> retrieveAnswersByUserId(Long user_id){
        return answerRepository.findByUserId(user_id);
    }

    public Optional<Answer> retrieveAnswer(Long id){
        return answerRepository.findById(id);
    }

    public Long getNumberOfAnswers(){
        return answerRepository.count();
    }

    @Transactional
    public void deleteAnswer(Long id){
        answerRepository.deleteById(id);
    }






}
