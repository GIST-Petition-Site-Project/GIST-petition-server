package com.example.gistcompetitioncnserver.verification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationInfoRepository extends JpaRepository<VerificationInfo, Long> {
    Optional<VerificationInfo> findByVerificationCode(String verificationCode);

    Optional<VerificationInfo> findByUsernameAndVerificationCode(String username, String verificationCode);
}
