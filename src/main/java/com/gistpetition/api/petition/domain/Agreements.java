package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.DuplicatedAgreementException;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Agreements {

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "petition", orphanRemoval = true)
    private final List<Agreement> agreementList = new ArrayList<>();

    protected Agreements() {

    }

    public void add(Agreement agreement) {
        if (agreementList.contains(agreement)) {
            throw new DuplicatedAgreementException();
        }
        this.agreementList.add(agreement);
    }

    public boolean isAgreedBy(Long userId) {
        return agreementList.stream().anyMatch(a -> a.writtenBy(userId));
    }

    public List<Agreement> getAgreementList() {
        return Collections.unmodifiableList(agreementList);
    }
}
