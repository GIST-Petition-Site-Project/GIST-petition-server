package com.example.gistcompetitioncnserver.verification.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationInfoRepository extends JpaRepository<VerificationInfo, Long> {
    Optional<VerificationInfo> findByVerificationCode(String verificationCode);

    Optional<VerificationInfo> findByUsernameAndVerificationCode(String username, String verificationCode);

    List<VerificationInfo> findByUsername(String username);

    void deleteByUsername(String username);
}