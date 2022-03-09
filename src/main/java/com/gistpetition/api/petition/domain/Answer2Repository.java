package com.gistpetition.api.petition.domain;

import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Answer2Repository extends RevisionRepository<Answer2, Long, Long> {
}
