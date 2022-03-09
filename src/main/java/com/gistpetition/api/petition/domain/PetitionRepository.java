package com.gistpetition.api.petition.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetitionRepository extends RevisionRepository<Petition, Long, Long>, JpaRepository<Petition, Long>, QuerydslPredicateExecutor<Petition>, CustomPetitionRepository {
    Page<Petition> findByUserId(Long userId, Pageable pageable);

    Page<Petition> findByTitleContains(String keyword, Pageable pageable);

    Page<Petition> findByAnswerIsNotNull(Pageable pageable);

    @Query("select p from Petition p where p.answer is not null")
    List<Petition> findByAnswerIsNotNull();

    Optional<Petition> findByTempUrl(String tempUrl);
}
