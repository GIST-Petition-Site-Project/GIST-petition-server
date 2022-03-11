package com.gistpetition.api.petition.domain.repository;

import com.gistpetition.api.petition.domain.Petition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface PetitionRepository extends RevisionRepository<Petition, Long, Long>, JpaRepository<Petition, Long>, CustomPetitionRepository {
    Page<Petition> findByTitleContains(String keyword, Pageable pageable);

    Optional<Petition> findByTempUrl(String tempUrl);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Petition p where p.id = ?1")
    Optional<Petition> findByIdWithPessimisticWriteLock(Long petitionId);
}
