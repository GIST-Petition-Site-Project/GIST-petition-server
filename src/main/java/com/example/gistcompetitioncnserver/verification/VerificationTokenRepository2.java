package com.example.gistcompetitioncnserver.verification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository2 extends JpaRepository<VerificationToken2, Long> {

    Optional<VerificationToken2> findByToken(String token);
}
