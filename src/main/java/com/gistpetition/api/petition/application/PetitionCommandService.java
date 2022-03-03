package com.gistpetition.api.petition.application;

import com.gistpetition.api.config.annotation.DataIntegrityHandler;
import com.gistpetition.api.exception.petition.DuplicatedAgreementException;
import com.gistpetition.api.petition.dto.AgreementRequest;
import com.gistpetition.api.petition.dto.PetitionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface PetitionCommandService {
    @Transactional
    Long createPetition(PetitionRequest petitionRequest, Long userId);

    @Transactional
    void updatePetition(Long petitionId, PetitionRequest petitionRequest);

    @Transactional
    void deletePetition(Long petitionId);

    @Transactional
    @DataIntegrityHandler(DuplicatedAgreementException.class)
    void agree(AgreementRequest request, Long petitionId, Long userId);

    @Transactional
    void cancelReleasePetition(Long petitionId);

    @Transactional
    void releasePetition(Long petitionId);
}
