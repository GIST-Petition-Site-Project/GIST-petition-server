package com.gistpetition.api.common.persistence;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class UnmodifiableEntity {
    @CreatedDate
    private LocalDateTime createdAt;

    protected UnmodifiableEntity() {
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
