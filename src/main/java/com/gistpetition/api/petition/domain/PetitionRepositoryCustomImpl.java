package com.gistpetition.api.petition.domain;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
public class PetitionRepositoryCustomImpl implements PetitionRepositoryCustom {

    @Override
    public Page<Petition> findPetitionByAgreeCountIsGreaterThanEqualAndReleasedFalse() {
        return null;
    }
}
