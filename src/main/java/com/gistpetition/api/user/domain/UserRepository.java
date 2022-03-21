package com.gistpetition.api.user.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    List<User> findAllByUsername(String username);

    Page<User> findAllByUserRole(UserRole userRole, Pageable pageable);

    List<User> findAllByUserRole(UserRole userRole);
}
