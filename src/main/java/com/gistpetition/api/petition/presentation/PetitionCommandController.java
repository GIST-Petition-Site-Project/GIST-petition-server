package com.gistpetition.api.petition.presentation;

import com.gistpetition.api.config.annotation.LoginRequired;
import com.gistpetition.api.config.annotation.LoginUser;
import com.gistpetition.api.config.annotation.ManagerPermissionRequired;
import com.gistpetition.api.petition.application.PetitionCommandService;
import com.gistpetition.api.petition.application.PetitionQueryService;
import com.gistpetition.api.petition.dto.AgreementRequest;
import com.gistpetition.api.petition.dto.AnswerRequest;
import com.gistpetition.api.petition.dto.PetitionRequest;
import com.gistpetition.api.petition.dto.RejectionRequest;
import com.gistpetition.api.user.domain.SimpleUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PetitionCommandController {
    private final PetitionQueryService petitionQueryService;
    private final PetitionCommandService petitionCommandService;

    @LoginRequired
    @PostMapping("/petitions")
    public ResponseEntity<Void> createPetition(@Validated @RequestBody PetitionRequest petitionRequest,
                                               @LoginUser SimpleUser simpleUser) {
        Long createdPetitionId = petitionCommandService.createPetition(petitionRequest, simpleUser.getId());
        String tempUrl = petitionQueryService.retrieveTempUrlOf(createdPetitionId);
        return ResponseEntity.created(URI.create("/v1/petitions/temp/" + tempUrl)).build();
    }

    @ManagerPermissionRequired
    @PutMapping("/petitions/{petitionId}")
    public ResponseEntity<Void> updatePetition(@PathVariable Long petitionId,
                                               @Validated @RequestBody PetitionRequest changeRequest) {
        petitionCommandService.updatePetition(petitionId, changeRequest);
        return ResponseEntity.noContent().build();
    }

    @ManagerPermissionRequired
    @DeleteMapping("/petitions/{petitionId}")
    public ResponseEntity<Void> deletePetition(@PathVariable Long petitionId) {
        petitionCommandService.deletePetition(petitionId);
        return ResponseEntity.noContent().build();
    }

    @ManagerPermissionRequired
    @PostMapping("/petitions/{petitionId}/release")
    public ResponseEntity<Void> releasePetition(@PathVariable Long petitionId) {
        petitionCommandService.releasePetition(petitionId);
        return ResponseEntity.noContent().build();
    }

    @ManagerPermissionRequired
    @DeleteMapping("/petitions/{petitionId}/release")
    public ResponseEntity<Void> cancelReleasePetition(@PathVariable Long petitionId) {
        petitionCommandService.cancelReleasePetition(petitionId);
        return ResponseEntity.noContent().build();
    }

    @LoginRequired
    @PostMapping("/petitions/{petitionId}/agreements")
    public ResponseEntity<Void> agreePetition(@RequestBody AgreementRequest agreementRequest,
                                              @PathVariable Long petitionId,
                                              @LoginUser SimpleUser simpleUser) {
        petitionCommandService.agree(agreementRequest, petitionId, simpleUser.getId());
        return ResponseEntity.ok().build();
    }

    @ManagerPermissionRequired
    @PostMapping("/petitions/{petitionId}/rejection")
    public ResponseEntity<Object> rejectPetition(@PathVariable Long petitionId,
                                               @Validated @RequestBody RejectionRequest rejectionRequest) {
        petitionCommandService.rejectPetition(petitionId, rejectionRequest);
        return ResponseEntity.created(URI.create("/v1/petitions/" + petitionId + "/rejection")).build();
    }

    @ManagerPermissionRequired
    @PutMapping("/petitions/{petitionId}/rejection")
    public ResponseEntity<Void> updateRejection(@PathVariable Long petitionId,
                                             @Validated @RequestBody RejectionRequest changeRequest) {
        petitionCommandService.updateRejection(petitionId, changeRequest);
        return ResponseEntity.ok().build();
    }

    @ManagerPermissionRequired
    @DeleteMapping("/petitions/{petitionId}/rejection")
    public ResponseEntity<Object> cancelRejection(@PathVariable Long petitionId) {
        petitionCommandService.cancelRejection(petitionId);
        return ResponseEntity.noContent().build();
    }

    @ManagerPermissionRequired
    @PostMapping("/petitions/{petitionId}/answer")
    public ResponseEntity<Object> createAnswer(@PathVariable Long petitionId,
                                               @Validated @RequestBody AnswerRequest answerRequest) {
        petitionCommandService.answerPetition(petitionId, answerRequest);
        return ResponseEntity.created(URI.create("/v1/petitions/" + petitionId + "/answer")).build();
    }

    @ManagerPermissionRequired
    @PutMapping("/petitions/{petitionId}/answer")
    public ResponseEntity<Void> updateAnswer(@PathVariable Long petitionId,
                                             @Validated @RequestBody AnswerRequest changeRequest) {
        petitionCommandService.updateAnswer(petitionId, changeRequest);
        return ResponseEntity.ok().build();
    }

    @ManagerPermissionRequired
    @DeleteMapping("/petitions/{petitionId}/answer")
    public ResponseEntity<Object> deleteAnswer(@PathVariable Long petitionId) {
        petitionCommandService.deleteAnswer(petitionId);
        return ResponseEntity.noContent().build();
    }
}
