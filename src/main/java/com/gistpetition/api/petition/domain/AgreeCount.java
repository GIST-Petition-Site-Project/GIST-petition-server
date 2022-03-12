package com.gistpetition.api.petition.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgreeCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "count", nullable = false)
    private Integer count;

    public AgreeCount(Integer count) {
        this(null, count);
    }

    private AgreeCount(Long id, Integer count) {
        this.id = id;
        this.count = count;
    }

    public void increment() {
        count += 1;
    }

    public boolean isLessThan(int count) {
        return this.count < count;
    }
}
