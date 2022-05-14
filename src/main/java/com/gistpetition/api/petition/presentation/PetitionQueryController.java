package com.gistpetition.api.petition.presentation;

import com.gistpetition.api.config.annotation.LoginRequired;
import com.gistpetition.api.config.annotation.LoginUser;
import com.gistpetition.api.config.annotation.ManagerPermissionRequired;
import com.gistpetition.api.petition.application.PetitionQueryDslDao;
import com.gistpetition.api.petition.application.PetitionQueryService;
import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.dto.*;
import com.gistpetition.api.user.domain.SimpleUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

import static com.gistpetition.api.petition.application.PetitionQueryCondition.*;
import static com.gistpetition.api.petition.domain.QPetition.petition;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PetitionQueryController {
    private static final String SEARCH_ALL = "0";
    private final PetitionQueryService petitionQueryService;
    private final PetitionQueryDslDao petitionQueryDslDao;

    @GetMapping("/petitions")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrieveNotTemporaryPetition(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId, Pageable pageable) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryDslDao.findAll(null, NOT_TEMPORARY.at(Instant.now()), pageable));
        }
        return ResponseEntity.ok().body(petitionQueryDslDao.findAll(Category.of(categoryId), NOT_TEMPORARY.at(Instant.now()), pageable));
    }

    @GetMapping("/petitions/ongoing")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrieveOngoingPetitions(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId, Pageable pageable) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryDslDao.findAll(null, ONGOING.at(Instant.now()), pageable));
        }
        return ResponseEntity.ok().body(petitionQueryDslDao.findAll(Category.of(categoryId), ONGOING.at(Instant.now()), pageable));
    }

    @GetMapping("/petitions/expired")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrieveExpiredPetition(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId, Pageable pageable) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryDslDao.findAll(null, EXPIRED.at(Instant.now()), pageable));
        }
        return ResponseEntity.ok().body(petitionQueryDslDao.findAll(Category.of(categoryId), EXPIRED.at(Instant.now()), pageable));
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/waitingForRelease")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrievePetitionsWaitingForRelease(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId, Pageable pageable) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryDslDao.findAll(null, WAITING_FOR_RELEASE.at(Instant.now()), pageable));
        }
        return ResponseEntity.ok().body(petitionQueryDslDao.findAll(Category.of(categoryId), WAITING_FOR_RELEASE.at(Instant.now()), pageable));
    }

    @GetMapping("/petitions/waitingForAnswer")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrievePetitionsWaitingForAnswer(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId, Pageable pageable) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryDslDao.findAll(null, WAITING_FOR_ANSWER.at(Instant.now()), pageable));
        }
        return ResponseEntity.ok().body(petitionQueryDslDao.findAll(Category.of(categoryId), WAITING_FOR_ANSWER.at(Instant.now()), pageable));
    }

    @GetMapping("/petitions/rejected")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrieveRejectedPetitions(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId, Pageable pageable) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryDslDao.findAll(null, REJECTED.at(Instant.now()), pageable));
        }
        return ResponseEntity.ok().body(petitionQueryDslDao.findAll(Category.of(categoryId), REJECTED.at(Instant.now()), pageable));
    }

    @GetMapping("/petitions/answered")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrieveAnsweredPetitions(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId, Pageable pageable) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryDslDao.findAll(null, ANSWERED.at(Instant.now()), pageable));
        }
        return ResponseEntity.ok().body(petitionQueryDslDao.findAll(Category.of(categoryId), ANSWERED.at(Instant.now()), pageable));
    }

    @GetMapping("/petitions/count")
    public ResponseEntity<Long> retrieveNotTemporaryPetitionCount(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryDslDao.count(null, NOT_TEMPORARY.at(Instant.now())));
        }
        return ResponseEntity.ok().body(petitionQueryDslDao.count(Category.of(categoryId), NOT_TEMPORARY.at(Instant.now())));
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/waitingForRelease/count")
    public ResponseEntity<Long> retrieveWaitingForReleasePetitionCount(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryDslDao.count(null, WAITING_FOR_RELEASE.at(Instant.now())));
        }
        return ResponseEntity.ok().body(petitionQueryDslDao.count(Category.of(categoryId), WAITING_FOR_RELEASE.at(Instant.now())));
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/waitingForAnswer/count")
    public ResponseEntity<Long> retrieveWaitingForAnswerPetitionCount(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryDslDao.count(null, WAITING_FOR_ANSWER.at(Instant.now())));
        }
        return ResponseEntity.ok().body(petitionQueryDslDao.count(Category.of(categoryId), WAITING_FOR_ANSWER.at(Instant.now())));
    }

    @GetMapping("/petitions/answered/count")
    public ResponseEntity<Long> retrieveAnsweredPetitionCount(@RequestParam(defaultValue = SEARCH_ALL) Long categoryId) {
        if (categoryId.equals(Long.valueOf(SEARCH_ALL))) {
            return ResponseEntity.ok().body(petitionQueryDslDao.count(null, ANSWERED.at(Instant.now())));
        }
        return ResponseEntity.ok().body(petitionQueryDslDao.count(Category.of(categoryId), ANSWERED.at(Instant.now())));
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
        return ResponseEntity.ok().body(petitionQueryDslDao.findAll(null, petition.userId.eq(simpleUser.getId()), pageable));
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

    @ManagerPermissionRequired
    @GetMapping("/petitions/{petitionId}/answer/revisions")
    public ResponseEntity<Page<AnswerRevisionResponse>> retrieveAnswerRevisions(@PathVariable Long petitionId, Pageable pageable) {
        return ResponseEntity.ok().body(petitionQueryService.retrieveRevisionsOfAnswer(petitionId, pageable));
    }
}
