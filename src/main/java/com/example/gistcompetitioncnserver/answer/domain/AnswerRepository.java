package com.example.gistcompetitioncnserver.answer.domain;

import com.example.gistcompetitioncnserver.answer.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByPostId(Long postId);

    boolean existsByPostId(Long postId);

    void deleteByPostId(Long postId);
}
