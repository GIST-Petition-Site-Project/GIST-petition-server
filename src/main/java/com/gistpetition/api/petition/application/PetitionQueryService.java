package com.gistpetition.api.petition.application;

import com.gistpetition.api.petition.dto.AgreementResponse;
import com.gistpetition.api.petition.dto.PetitionPreviewResponse;
import com.gistpetition.api.petition.dto.PetitionResponse;
import com.gistpetition.api.petition.dto.PetitionRevisionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface PetitionQueryService {

    @Transactional(readOnly = true)
    Page<PetitionPreviewResponse> retrievePetition(Pageable pageable);

    @Transactional(readOnly = true)
    Page<PetitionPreviewResponse> retrieveReleasedPetition(Pageable pageable);

    @Transactional(readOnly = true)
    Page<PetitionPreviewResponse> retrieveReleasedPetitionByCategoryId(Long categoryId, Pageable pageable);

    @Transactional(readOnly = true)
    Page<PetitionPreviewResponse> retrieveReleasedAndExpiredPetitionByCategoryId(Long categoryId, Pageable pageable);

    @Transactional(readOnly = true)
    Page<PetitionPreviewResponse> retrieveOngoingPetitionByCategoryId(Long categoryId, Pageable pageable);

    @Transactional(readOnly = true)
    Page<PetitionPreviewResponse> retrievePetitionByKeyword(String keyword, Pageable pageable);

    @Transactional(readOnly = true)
    Page<PetitionPreviewResponse> retrievePetitionsByUserId(Long userId, Pageable pageable);

    @Transactional(readOnly = true)
    Page<PetitionPreviewResponse> retrievePetitionsWaitingForRelease(Pageable pageable);

    @Transactional(readOnly = true)
    Page<PetitionPreviewResponse> retrievePetitionsWaitingForAnswer(Pageable pageable);

    @Transactional(readOnly = true)
    Long retrieveWaitingForReleasePetitionCount();

    @Transactional(readOnly = true)
    Long retrieveWaitingForAnswerPetitionCount();

    @Transactional(readOnly = true)
    PetitionResponse retrieveReleasedPetitionById(Long petitionId);

    @Transactional(readOnly = true)
    Page<PetitionPreviewResponse> retrieveAnsweredPetition(Pageable pageable);

    @Transactional(readOnly = true)
    Page<PetitionRevisionResponse> retrieveRevisionsOfPetition(Long petitionId, Pageable pageable);

    @Transactional(readOnly = true)
    Long retrieveReleasedPetitionCount();

    @Transactional(readOnly = true)
    Long retrieveAnsweredPetitionCount();

    @Transactional(readOnly = true)
    Page<AgreementResponse> retrieveAgreements(Long petitionId, Pageable pageable);

    @Transactional(readOnly = true)
    int retrieveNumberOfAgreements(Long petitionId);

    @Transactional(readOnly = true)
    Boolean retrieveStateOfAgreement(Long petitionId, Long userId);

    @Transactional
    void releasePetition(Long petitionId);

    @Transactional(readOnly = true)
    PetitionResponse retrievePetitionByTempUrl(String tempUrl);

    @Transactional(readOnly = true)
    String retrieveTempUrlOf(Long petitionId);
}
