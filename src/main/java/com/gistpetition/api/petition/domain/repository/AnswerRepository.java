package com.gistpetition.api.petition.domain.repository;

import com.gistpetition.api.petition.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAllByPetitionId(Long petitionId);
}
