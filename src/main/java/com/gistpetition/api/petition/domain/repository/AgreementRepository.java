package com.gistpetition.api.petition.domain.repository;

import com.gistpetition.api.petition.domain.Agreement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgreementRepository extends JpaRepository<Agreement, Long> {
    Page<Agreement> findAgreementsByPetitionId(Long petitionId, Pageable pageable);

    Optional<Agreement> findByUserId(Long userId);
}
