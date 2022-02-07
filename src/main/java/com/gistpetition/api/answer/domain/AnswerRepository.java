package com.gistpetition.api.answer.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends RevisionRepository<Answer, Long, Long>, JpaRepository<Answer, Long> {
    Optional<Answer> findByPetitionId(Long petitionId);

    boolean existsByPetitionId(Long petitionId);

    void deleteByPetitionId(Long petitionId);
}
