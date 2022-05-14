package com.gistpetition.api.petition.domain.repository;

import com.gistpetition.api.petition.domain.AgreeCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface AgreeCountRepository extends JpaRepository<AgreeCount, Long> {

    Optional<AgreeCount> findByPetitionId(Long petitionId);

    @Modifying
    @Query("update AgreeCount a set a.count = a.count + 1 where a.petitionId = ?1")
    void incrementCount(Long petitionId);
}
