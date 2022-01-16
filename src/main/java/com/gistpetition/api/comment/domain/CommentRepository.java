package com.gistpetition.api.comment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPetitionId(Long petitionId);

    void deleteByPetitionId(Long petitionId);
}
