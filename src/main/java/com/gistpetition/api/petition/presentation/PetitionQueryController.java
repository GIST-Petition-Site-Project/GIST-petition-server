package com.gistpetition.api.petition.presentation;

import com.gistpetition.api.config.annotation.LoginRequired;
import com.gistpetition.api.config.annotation.LoginUser;
import com.gistpetition.api.config.annotation.ManagerPermissionRequired;
import com.gistpetition.api.petition.application.PetitionQueryService;
import com.gistpetition.api.petition.domain.Category;
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

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PetitionQueryController {
    private final PetitionQueryService petitionQueryService;

    @GetMapping("/petitions")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrieveReleasedPetitions(@RequestParam Optional<Long> categoryId,
                                                                                   @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok().body(petitionQueryService.retrieveReleasedPetition(Category.of(categoryId), pageable));
    }

    @GetMapping("/petitions/count")
    public ResponseEntity<Long> retrieveReleasedPetitionCount(@RequestParam Optional<Long> categoryId) {
        return ResponseEntity.ok().body(petitionQueryService.retrieveReleasedPetitionCount(Category.of(categoryId)));
    }

    @GetMapping("/petitions/ongoing")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrieveOngoingPetitions(@RequestParam Optional<Long> categoryId,
                                                                                  @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(petitionQueryService.retrieveOngoingPetition(Category.of(categoryId), pageable));
    }

    @GetMapping("/petitions/expired")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrieveReleasedAndExpiredPetitions(@RequestParam Optional<Long> categoryId,
                                                                                             @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(petitionQueryService.retrieveReleasedAndExpiredPetition(Category.of(categoryId), pageable));
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/waitingForRelease")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrievePetitionsWaitingForCheck(@RequestParam Optional<Long> categoryId,
                                                                                          @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(petitionQueryService.retrievePetitionsWaitingForRelease(Category.of(categoryId), pageable));
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/waitingForAnswer")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrievePetitionsWaitingForAnswer(@RequestParam Optional<Long> categoryId,
                                                                                           @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(petitionQueryService.retrievePetitionsWaitingForAnswer(Category.of(categoryId), pageable));
    }

    @GetMapping("/petitions/answered")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrieveAnsweredPetitions(@RequestParam Optional<Long> categoryId,
                                                                                   @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(petitionQueryService.retrieveAnsweredPetition(Category.of(categoryId), pageable));
    }

    @GetMapping("/petitions/answered/count")
    public ResponseEntity<Long> retrieveAnsweredPetitionCount(@RequestParam Optional<Long> categoryId) {
        return ResponseEntity.ok().body(petitionQueryService.retrieveAnsweredPetitionCount(Category.of(categoryId)));
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/waitingForRelease/count")
    public ResponseEntity<Long> retrievePetitionsWaitingForCheckCount(@RequestParam Optional<Long> categoryId) {
        return ResponseEntity.ok().body(petitionQueryService.retrieveWaitingForReleasePetitionCount(Category.of(categoryId)));
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/waitingForAnswer/count")
    public ResponseEntity<Long> retrievePetitionsWaitingForAnswerCount(@RequestParam Optional<Long> categoryId) {
        return ResponseEntity.ok().body(petitionQueryService.retrieveWaitingForAnswerPetitionCount(Category.of(categoryId)));
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
    public ResponseEntity<Page<PetitionPreviewResponse>> retrievePetitionsOfMine(@LoginUser SimpleUser simpleUser,
                                                                                 @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(petitionQueryService.retrievePetitionsByUserId(simpleUser.getId(), pageable));
    }

    @ManagerPermissionRequired
    @GetMapping("/petitions/{petitionId}/revisions")
    public ResponseEntity<Page<PetitionRevisionResponse>> retrieveRevisionsOfPetition(@PathVariable Long petitionId,
                                                                                      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(petitionQueryService.retrieveRevisionsOfPetition(petitionId, pageable));
    }

    @GetMapping("/petitions/search")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrievePetitionsByKeyword(@RequestParam(defaultValue = "") String keyword,
                                                                                    @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (keyword.equals("")) {
            return ResponseEntity.ok().body(petitionQueryService.retrievePetition(pageable));
        }
        return ResponseEntity.ok().body(petitionQueryService.retrievePetitionByKeyword(keyword, pageable));
    }

    @GetMapping("/petitions/{petitionId}/agreements")
    public ResponseEntity<Page<AgreementResponse>> retrieveAgreements(@PathVariable Long petitionId,
                                                                      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(petitionQueryService.retrieveAgreements(petitionId, pageable));
    }

    @LoginRequired
    @GetMapping("/petitions/{petitionId}/agreements/me")
    public ResponseEntity<Boolean> retrieveStateOfAgreement(@PathVariable Long petitionId,
                                                            @LoginUser SimpleUser simpleUser) {
        return ResponseEntity.ok().body(petitionQueryService.retrieveStateOfAgreement(petitionId, simpleUser.getId()));
    }
}
