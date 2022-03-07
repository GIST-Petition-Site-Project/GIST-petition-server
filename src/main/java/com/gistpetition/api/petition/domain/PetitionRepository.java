package com.gistpetition.api.petition.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetitionRepository extends RevisionRepository<Petition, Long, Long>, JpaRepository<Petition, Long>, QuerydslPredicateExecutor<Petition> {
    Page<Petition> findByUserId(Long userId, Pageable pageable);

    Page<Petition> findByTitleContains(String keyword, Pageable pageable);

    Optional<Petition> findByTempUrl(String tempUrl);
}
