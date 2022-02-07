package com.gistpetition.api.verification.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordVerificationInfoRepository extends JpaRepository<PasswordVerificationInfo, Long> {
    Optional<PasswordVerificationInfo> findByVerificationCode(String verificationCode);

    Optional<PasswordVerificationInfo> findByUsernameAndVerificationCode(String username, String verificationCode);

    List<PasswordVerificationInfo> findByUsername(String username);

    void deleteByUsername(String username);
}
