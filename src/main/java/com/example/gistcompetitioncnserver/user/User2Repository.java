package com.example.gistcompetitioncnserver.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface User2Repository extends JpaRepository<User2, Long> {
    Boolean existsByUsername(String username);

    Optional<User2> findByUsername(String userName);
}
