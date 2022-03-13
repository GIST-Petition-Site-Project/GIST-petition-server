package com.gistpetition.api.petition.domain.repository;

import com.gistpetition.api.petition.domain.Petition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetitionRepository extends RevisionRepository<Petition, Long, Long>, JpaRepository<Petition, Long> {
    Optional<Petition> findByTempUrl(String tempUrl);
}
