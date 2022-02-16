package com.gistpetition.api.petition.presentation;

import com.gistpetition.api.config.annotation.AdminPermissionRequired;
import com.gistpetition.api.config.annotation.LoginRequired;
import com.gistpetition.api.config.annotation.LoginUser;
import com.gistpetition.api.config.annotation.ManagerPermissionRequired;
import com.gistpetition.api.exception.petition.DuplicatedAgreementException;
import com.gistpetition.api.petition.application.PetitionService;
import com.gistpetition.api.petition.application.TempPetitionService;
import com.gistpetition.api.petition.dto.*;
import com.gistpetition.api.user.domain.SimpleUser;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PetitionController {
    private final PetitionService petitionService;
    private final TempPetitionService tempPetitionService;

    @LoginRequired
    @PostMapping("/petitions")
    public ResponseEntity<Void> createPetition(@Validated @RequestBody PetitionRequest petitionRequest,
                                               @LoginUser SimpleUser simpleUser) {
        Long createdPetitionId = petitionService.createPetition(petitionRequest, simpleUser.getId());
        String tempUrl = tempPetitionService.createTempUrl(createdPetitionId);
        return ResponseEntity.created(URI.create("/petitions/temp/" + tempUrl)).build();
    }

    @GetMapping("/petitions")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrievePetitions(@RequestParam(defaultValue = "0") Long categoryId,
                                                                           @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (categoryId.equals(0L)) {
            return ResponseEntity.ok().body(petitionService.retrievePetition(pageable));
        }
        return ResponseEntity.ok().body(petitionService.retrievePetitionByCategoryId(categoryId, pageable));
    }

    @GetMapping("/petitions/answered")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrieveAnsweredPetitions(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(petitionService.retrieveAnsweredPetition(pageable));
    }

    @GetMapping("/petitions/search")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrievePetitionsByKeyword(@RequestParam(defaultValue = "") String keyword,
                                                                                    @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (keyword.equals("")) {
            return ResponseEntity.ok().body(petitionService.retrievePetition(pageable));
        }
        return ResponseEntity.ok().body(petitionService.retrievePetitionByKeyword(keyword, pageable));
    }

    @GetMapping("/petitions/{petitionId}")
    public ResponseEntity<PetitionResponse> retrievePetition(@PathVariable Long petitionId) {
        return ResponseEntity.ok().body(petitionService.retrievePetitionById(petitionId));
    }

    @LoginRequired
    @GetMapping("/petitions/me")
    public ResponseEntity<Page<PetitionPreviewResponse>> retrievePetitionsOfMine(@LoginUser SimpleUser simpleUser,
                                                                                 @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(petitionService.retrievePetitionsByUserId(simpleUser.getId(), pageable));
    }

    @AdminPermissionRequired
    @GetMapping("/petitions/{petitionId}/revisions")
    public ResponseEntity<Page<PetitionRevisionResponse>> retrieveRevisionsOfPetition(@PathVariable Long petitionId,
                                                                                      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(petitionService.retrieveRevisionsOfPetition(petitionId, pageable));
    }

    @GetMapping("/petitions/count")
    public ResponseEntity<Long> retrievePetitionCount() {
        return ResponseEntity.ok().body(petitionService.retrievePetitionCount());
    }

    @ManagerPermissionRequired
    @PutMapping("/petitions/{petitionId}")
    public ResponseEntity<Void> updatePetition(@PathVariable Long petitionId,
                                               @Validated @RequestBody PetitionRequest changeRequest) {
        petitionService.updatePetition(petitionId, changeRequest);
        return ResponseEntity.noContent().build();
    }

    @ManagerPermissionRequired
    @DeleteMapping("/petitions/{petitionId}")
    public ResponseEntity<Void> deletePetition(@PathVariable Long petitionId) {
        petitionService.deletePetition(petitionId);
        return ResponseEntity.noContent().build();
    }

    @LoginRequired
    @PostMapping("/petitions/{petitionId}/agreements")
    public ResponseEntity<Void> agreePetition(@RequestBody AgreementRequest agreementRequest,
                                              @PathVariable Long petitionId,
                                              @LoginUser SimpleUser simpleUser) {
        try {
            petitionService.agree(agreementRequest, petitionId, simpleUser.getId());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedAgreementException();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/petitions/{petitionId}/agreements")
    public ResponseEntity<Page<AgreementResponse>> retrieveAgreements(@PathVariable Long petitionId,
                                                                      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(petitionService.retrieveAgreements(petitionId, pageable));
    }

    @GetMapping("/petitions/{petitionId}/agreements/number")
    public ResponseEntity<Integer> retrieveNumberOfAgreement(@PathVariable Long petitionId) {
        return ResponseEntity.ok().body(petitionService.retrieveNumberOfAgreements(petitionId));
    }

    @LoginRequired
    @GetMapping("/petitions/{petitionId}/agreements/me")
    public ResponseEntity<Boolean> retrieveStateOfAgreement(@PathVariable Long petitionId,
                                                            @LoginUser SimpleUser simpleUser) {
        return ResponseEntity.ok().body(petitionService.retrieveStateOfAgreement(petitionId, simpleUser.getId()));
    }

    @GetMapping("/petitions/temp/{tempUrl}")
    public ResponseEntity<TempPetitionResponse> retrieveTempPetition(@PathVariable String tempUrl) {
        Long petitionId = tempPetitionService.findPetitionIdByTempUrl(tempUrl);
        return ResponseEntity.ok().body(petitionService.retrieveTempPetitionById(petitionId, tempUrl));
    }
}
