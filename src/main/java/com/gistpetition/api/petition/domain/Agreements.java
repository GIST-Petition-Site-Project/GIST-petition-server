package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.DuplicatedAgreementException;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.NotAudited;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Agreements {
    @NotAudited
    @BatchSize(size = 10)
    @OneToMany(mappedBy = "petition", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<Agreement> agreements = new ArrayList<>();

    protected Agreements() {
    }

    public void add(Agreement agreement) {
        if (agreements.contains(agreement)) {
            throw new DuplicatedAgreementException();
        }
        this.agreements.add(agreement);
    }

    public boolean agreeLessThan(int agreeCount) {
        return this.agreements.size() < agreeCount;
    }

    public boolean isAgreedBy(Long userId) {
        return agreements.stream().anyMatch(a -> a.writtenBy(userId));
    }

    public List<Agreement> getAgreements() {
        return Collections.unmodifiableList(agreements);
    }

    public int size() {
        return agreements.size();
    }

    public boolean hasSize(int size) {
        return agreements.size() == size;
    }
}
