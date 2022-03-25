package com.gistpetition.api.petition.domain.repository;

import com.gistpetition.api.petition.domain.Rejection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RejectionRepository extends JpaRepository<Rejection, Long> {
    Optional<Rejection> findByPetitionId(Long petitionId);
}
