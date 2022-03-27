package com.gistpetition.api.petition.domain.repository;

import com.gistpetition.api.petition.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends RevisionRepository<Answer, Long, Long>, JpaRepository<Answer, Long> {
    List<Answer> findAllByPetitionId(Long petitionId);

    Optional<Answer> findByPetitionId(Long petitionId);
}
