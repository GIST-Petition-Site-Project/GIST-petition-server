package com.gistpetition.api.petition.domain;

import org.springframework.data.domain.Page;

public interface PetitionRepositoryCustom {

    Page<Petition> findPetitionByAgreeCountIsGreaterThanEqualAndReleasedFalse() ;

}
