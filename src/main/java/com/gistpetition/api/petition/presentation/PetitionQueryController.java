package com.gistpetition.api.petition.presentation;

import com.gistpetition.api.config.annotation.LoginRequired;
import com.gistpetition.api.config.annotation.LoginUser;
import com.gistpetition.api.config.annotation.ManagerPermissionRequired;
import com.gistpetition.api.petition.application.PetitionQueryService;
import com.gistpetition.api.petition.domain.Answer;
import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.dto.*;
import com.gistpetition.api.user.domain.SimpleUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PetitionQueryController {
    private static final String SEARCH_ALL = "0";
    private final PetitionQueryService petitionQueryService;

    @GetMapping("/petitions")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrieveReleasedPetitions(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId, Pageable pageable) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryService.retrieveReleasedPetition(pageable));
        }
        return ResponseEntity.ok().body(petitionQueryService.retrieveReleasedPetition(Category.of(categoryId), pageable));
    }

    @GetMapping("/petitions/ongoing")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrieveOngoingPetitions(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId, Pageable pageable) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryService.retrieveOngoingPetition(pageable));
        }
        return ResponseEntity.ok().body(petitionQueryService.retrieveOngoingPetition(Category.of(categoryId), pageable));
    }

    @GetMapping("/petitions/expired")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrieveReleasedAndExpiredPetitions(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId, Pageable pageable) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryService.retrieveReleasedAndExpiredPetition(pageable));
        }
        return ResponseEntity.ok().body(petitionQueryService.retrieveReleasedAndExpiredPetition(Category.of(categoryId), pageable));
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/waitingForRelease")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrievePetitionsWaitingForRelease(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId, Pageable pageable) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryService.retrievePetitionsWaitingForRelease(pageable));
        }
        return ResponseEntity.ok().body(petitionQueryService.retrievePetitionsWaitingForRelease(Category.of(categoryId), pageable));
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/waitingForAnswer")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrievePetitionsWaitingForAnswer(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId, Pageable pageable) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryService.retrievePetitionsWaitingForAnswer(pageable));
        }
        return ResponseEntity.ok().body(petitionQueryService.retrievePetitionsWaitingForAnswer(Category.of(categoryId), pageable));
    }

    @GetMapping("/petitions/answered")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrieveAnsweredPetitions(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId, Pageable pageable) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryService.retrieveAnsweredPetition(pageable));
        }
        return ResponseEntity.ok().body(petitionQueryService.retrieveAnsweredPetition(Category.of(categoryId), pageable));
    }

    @GetMapping("/petitions/count")
    public ResponseEntity<Long> retrieveReleasedPetitionCount(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryService.retrieveReleasedPetitionCount());
        }
        return ResponseEntity.ok().body(petitionQueryService.retrieveReleasedPetitionCount(Category.of(categoryId)));
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/waitingForRelease/count")
    public ResponseEntity<Long> retrievePetitionsWaitingForCheckCount(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryService.retrieveWaitingForReleasePetitionCount());
        }
        return ResponseEntity.ok().body(petitionQueryService.retrieveWaitingForReleasePetitionCount(Category.of(categoryId)));
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/waitingForAnswer/count")
    public ResponseEntity<Long> retrievePetitionsWaitingForAnswerCount(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryService.retrieveWaitingForAnswerPetitionCount());
        }
        return ResponseEntity.ok().body(petitionQueryService.retrieveWaitingForAnswerPetitionCount(Category.of(categoryId)));
    }

    @GetMapping("/petitions/answered/count")
    public ResponseEntity<Long> retrieveAnsweredPetitionCount(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryService.retrieveAnsweredPetitionCount());
        }
        return ResponseEntity.ok().body(petitionQueryService.retrieveAnsweredPetitionCount(Category.of(categoryId)));
    }

    @GetMapping("/petitions/{petitionId}")
    public ResponseEntity<PetitionResponse> retrieveReleasedPetition(@PathVariable Long petitionId) {
        return ResponseEntity.ok().body(petitionQueryService.retrieveReleasedPetitionById(petitionId));
    }

    @GetMapping("/petitions/temp/{tempUrl}")
    public ResponseEntity<PetitionResponse> retrieveTempPetition(@PathVariable String tempUrl) {
        return ResponseEntity.ok().body(petitionQueryService.retrievePetitionByTempUrl(tempUrl));
    }

    @LoginRequired
    @GetMapping("/petitions/me")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrievePetitionsOfMine(@LoginUser SimpleUser simpleUser, Pageable pageable) {
        return ResponseEntity.ok().body(petitionQueryService.retrievePetitionsByUserId(simpleUser.getId(), pageable));
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/{petitionId}/revisions")
    public ResponseEntity<Page<PetitionRevisionResponse>> retrieveRevisionsOfPetition(@PathVariable Long petitionId, Pageable pageable) {
        return ResponseEntity.ok().body(petitionQueryService.retrieveRevisionsOfPetition(petitionId, pageable));
    }

    @GetMapping("/petitions/{petitionId}/agreements")
    public ResponseEntity<Page<AgreementResponse>> retrieveAgreements(@PathVariable Long petitionId, Pageable pageable) {
        return ResponseEntity.ok().body(petitionQueryService.retrieveAgreements(petitionId, pageable));
    }

    @LoginRequired
    @GetMapping("/petitions/{petitionId}/agreements/me")
    public ResponseEntity<Boolean> retrieveStateOfAgreement(@PathVariable Long petitionId,
                                                            @LoginUser SimpleUser simpleUser) {
        return ResponseEntity.ok().body(petitionQueryService.retrieveStateOfAgreement(petitionId, simpleUser.getId()));
    }

    @GetMapping("/petitions/{petitionId}/answer")
    public ResponseEntity<Answer> retrieveAnswer(@PathVariable Long petitionId) {
        return ResponseEntity.ok().body(petitionQueryService.retrieveAnswerByPetitionId(petitionId));
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/{petitionId}/answer/revisions")
    public ResponseEntity<Page<AnswerRevisionResponse>> retrieveAnswerRevisions(@PathVariable Long petitionId, Pageable pageable) {
        return ResponseEntity.ok().body(petitionQueryService.retrieveRevisionsOfAnswer(petitionId, pageable));
    }
}
