package com.gistpetition.api.petition.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgreementRepository extends JpaRepository<Agreement, Long> {
    Page<Agreement> findAgreementsByPetitionId(Pageable pageable, Long petitionId);

    Optional<Agreement> findByUserId(Long userId);
}
