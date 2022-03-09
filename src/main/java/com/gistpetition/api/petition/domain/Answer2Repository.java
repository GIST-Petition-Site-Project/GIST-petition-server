package com.gistpetition.api.petition.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Answer2Repository extends RevisionRepository<Answer2, Long, Long>, JpaRepository<Answer2, Long> {
    List<Answer2> findByPetitionId(Long petitionId);
}
