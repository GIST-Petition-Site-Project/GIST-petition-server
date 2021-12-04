package com.example.gistcompetitioncnserver.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgreementRepository extends JpaRepository<LikeToPost, Long> {
}
