package com.gistpetition.api.verification.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SignUpVerificationInfoRepository extends JpaRepository<SignUpVerificationInfo, Long> {
    Optional<SignUpVerificationInfo> findByVerificationCode(String verificationCode);

    Optional<SignUpVerificationInfo> findByUsernameAndVerificationCode(String username, String verificationCode);

    List<SignUpVerificationInfo> findByUsername(String username);

    void deleteByUsername(String username);
}
