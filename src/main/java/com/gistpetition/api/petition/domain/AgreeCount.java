package com.gistpetition.api.petition.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "agree_count")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgreeCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "count", nullable = false)
    private Integer count;

    @Column(name = "petition_id", unique = true, nullable = false)
    private Long petitionId;

    public AgreeCount(Long petitionId) {
        this(null, 0, petitionId);
    }

    private AgreeCount(Long id, Integer count, Long petitionId) {
        this.id = id;
        this.count = count;
        this.petitionId = petitionId;
    }

    public void increment() {
        count += 1;
    }

    public boolean isLessThan(int count) {
        return this.count < count;
    }
}
