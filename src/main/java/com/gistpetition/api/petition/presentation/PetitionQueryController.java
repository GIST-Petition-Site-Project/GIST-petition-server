package com.gistpetition.api.petition.presentation;

import com.gistpetition.api.config.annotation.LoginRequired;
import com.gistpetition.api.config.annotation.LoginUser;
import com.gistpetition.api.config.annotation.ManagerPermissionRequired;
import com.gistpetition.api.petition.application.PetitionService;
import com.gistpetition.api.petition.dto.AgreementResponse;
import com.gistpetition.api.petition.dto.PetitionPreviewResponse;
import com.gistpetition.api.petition.dto.PetitionResponse;
import com.gistpetition.api.petition.dto.PetitionRevisionResponse;
import com.gistpetition.api.user.domain.SimpleUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PetitionQueryController {
    private final PetitionService petitionService;

    @GetMapping("/petitions/ongoing")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrieveOngoingPetitions(@RequestParam(defaultValue = "0") Long categoryId,
                                                                                  @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (categoryId.equals(0L)) {
            return ResponseEntity.ok().body(petitionService.retrieveOngoingPetition(pageable));
        }
        return ResponseEntity.ok().body(petitionService.retrieveOngoingPetitionByCategoryId(categoryId, pageable));
    }

    @GetMapping("/petitions/expired")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrieveReleasedAndExpiredPetitions(@RequestParam(defaultValue = "0") Long categoryId,
                                                                                             @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (categoryId.equals(0L)) {
            return ResponseEntity.ok().body(petitionService.retrieveReleasedAndExpiredPetition(pageable));
        }
        return ResponseEntity.ok().body(petitionService.retrieveReleasedAndExpiredPetitionByCategoryId(categoryId, pageable));
    }

    @GetMapping("/petitions/answered")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrieveAnsweredPetitions(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(petitionService.retrieveAnsweredPetition(pageable));
    }

    @GetMapping("/petitions/answered/count")
    public ResponseEntity<Long> retrieveAnsweredPetitionCount() {
        return ResponseEntity.ok().body(petitionService.retrieveAnsweredPetitionCount());
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/waitingForRelease")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrievePetitionsWaitingForCheck(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(petitionService.retrievePetitionsWaitingForRelease(pageable));
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/waitingForRelease/count")
    public ResponseEntity<Long> retrievePetitionsWaitingForCheckCount() {
        return ResponseEntity.ok().body(petitionService.retrieveWaitingForReleasePetitionCount());
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/waitingForAnswer")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrievePetitionsWaitingForAnswer(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(petitionService.retrievePetitionsWaitingForAnswer(pageable));
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/waitingForAnswer/count")
    public ResponseEntity<Long> retrievePetitionsWaitingForAnswerCount() {
        return ResponseEntity.ok().body(petitionService.retrieveWaitingForAnswerPetitionCount());
    }

    @GetMapping("/petitions/count")
    public ResponseEntity<Long> retrieveReleasedPetitionCount() {
        return ResponseEntity.ok().body(petitionService.retrieveReleasedPetitionCount());
    }

    @GetMapping("/petitions/{petitionId}")
    public ResponseEntity<PetitionResponse> retrieveReleasedPetition(@PathVariable Long petitionId) {
        return ResponseEntity.ok().body(petitionService.retrieveReleasedPetitionById(petitionId));
    }

    @GetMapping("/petitions/temp/{tempUrl}")
    public ResponseEntity<PetitionResponse> retrieveTempPetition(@PathVariable String tempUrl) {
        return ResponseEntity.ok().body(petitionService.retrievePetitionByTempUrl(tempUrl));
    }

    @LoginRequired
    @GetMapping("/petitions/me")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrievePetitionsOfMine(@LoginUser SimpleUser simpleUser,
                                                                                 @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(petitionService.retrievePetitionsByUserId(simpleUser.getId(), pageable));
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/{petitionId}/revisions")
    public ResponseEntity<Page<PetitionRevisionResponse>> retrieveRevisionsOfPetition(@PathVariable Long petitionId,
                                                                                      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(petitionService.retrieveRevisionsOfPetition(petitionId, pageable));
    }

    @GetMapping("/petitions/search")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrievePetitionsByKeyword(@RequestParam(defaultValue = "") String keyword,
                                                                                    @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (keyword.equals("")) {
            return ResponseEntity.ok().body(petitionService.retrievePetition(pageable));
        }
        return ResponseEntity.ok().body(petitionService.retrievePetitionByKeyword(keyword, pageable));
    }

    @GetMapping("/petitions/{petitionId}/agreements")
    public ResponseEntity<Page<AgreementResponse>> retrieveAgreements(@PathVariable Long petitionId,
                                                                      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(petitionService.retrieveAgreements(petitionId, pageable));
    }

    @LoginRequired
    @GetMapping("/petitions/{petitionId}/agreements/me")
    public ResponseEntity<Boolean> retrieveStateOfAgreement(@PathVariable Long petitionId,
                                                            @LoginUser SimpleUser simpleUser) {
        return ResponseEntity.ok().body(petitionService.retrieveStateOfAgreement(petitionId, simpleUser.getId()));
    }
}
