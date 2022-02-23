package com.gistpetition.api.petition.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PetitionRepository extends RevisionRepository<Petition, Long, Long>, JpaRepository<Petition, Long> {
    Page<Petition> findByUserId(Long userId, Pageable pageable);

    Page<Petition> findAllByCategory(Category category, Pageable pageable);

    Page<Petition> findAll(Pageable pageable);

    Page<Petition> findByTitleContains(String keyword, Pageable pageable);

    Page<Petition> findByAnsweredTrue(Pageable pageable);

    Page<Petition> findAllByOrderByAgreeCountDesc(Pageable pageable);

    Page<Petition> findPetitionByAgreeCountIsGreaterThanEqualAndReleasedFalse(int requiredAgreeCount, Pageable pageable);

    Page<Petition> findAllByExpiredAtBeforeAndReleasedTrue(LocalDateTime at, Pageable pageable);

    Page<Petition> findAllByCategoryAndExpiredAtBeforeAndReleasedTrue(Category category, LocalDateTime at, Pageable pageable);

    Page<Petition> findAllByExpiredAtAfterAndReleasedTrueAndAnsweredFalse(LocalDateTime at, Pageable pageable);

    Page<Petition> findAllByCategoryAndExpiredAtAfterAndReleasedTrueAndAnsweredFalse(Category category, LocalDateTime at, Pageable pageable);
  
    Page<Petition> findPetitionByAgreeCountIsGreaterThanEqualAndReleasedTrueAndAnsweredFalse(int requiredAnswerCount, Pageable pageable);
    
    Long countByReleasedTrue();

    @Query("SELECT p FROM Petition AS p LEFT JOIN FETCH p.agreements WHERE p.id=:petitionId")
    Petition findPetitionByWithEagerMode(@Param("petitionId") Long petitionId);

    Optional<Petition> findByTempUrl(String tempUrl);

    Long countByAgreeCountIsGreaterThanEqualAndReleasedFalse(int requiredReleaseCount);

    Long countByAgreeCountIsGreaterThanEqualAndReleasedTrueAndAnsweredFalse(int requiredAnswerCount);
}
