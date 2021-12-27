package com.example.gistcompetitioncnserver.answer;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostRepository;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRepository;
import com.example.gistcompetitioncnserver.user.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public AnswerService (AnswerRepository answerRepository,
                          PostRepository postRepository,
                          UserRepository userRepository) {
        this.answerRepository = answerRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Long createAnswer(Long postId, AnswerRequestDto answerRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new CustomException("존재하지 않는 user입니다"));
        if (user.getUserRole() != UserRole.MANAGER){
            throw new CustomException("답변권한이 없는 user입니다.");
        }
        Post post = postRepository.findById(postId).orElseThrow(()-> new CustomException("존재하지 않는 post입니다"));
        if (post.isAnswered()){
            throw new CustomException("이미 답변이 된 post입니다.");
        }

        Answer answer = new Answer(answerRequestDto.getContent(), postId, userId);
        post.setAnswered(true);
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
