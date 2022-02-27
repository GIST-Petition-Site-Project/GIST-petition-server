package com.gistpetition.api.petition.presentation;

import com.gistpetition.api.config.annotation.LoginRequired;
import com.gistpetition.api.config.annotation.LoginUser;
import com.gistpetition.api.config.annotation.ManagerPermissionRequired;
import com.gistpetition.api.petition.application.PetitionService;
import com.gistpetition.api.petition.dto.AgreementRequest;
import com.gistpetition.api.petition.dto.PetitionRequest;
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
    private final PetitionService petitionService;

    @LoginRequired
    @PostMapping("/petitions")
    public ResponseEntity<Void> createPetition(@Validated @RequestBody PetitionRequest petitionRequest,
                                               @LoginUser SimpleUser simpleUser) {
        Long createdPetitionId = petitionService.createPetition(petitionRequest, simpleUser.getId());
        String tempUrl = petitionService.retrieveTempUrlOf(createdPetitionId);
        return ResponseEntity.created(URI.create("/v1/petitions/temp/" + tempUrl)).build();
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

    @ManagerPermissionRequired
    @PostMapping("/petitions/{petitionId}/release")
    public ResponseEntity<Void> releasePetition(@PathVariable Long petitionId) {
        petitionService.releasePetition(petitionId);
        return ResponseEntity.noContent().build();
    }

    @ManagerPermissionRequired
    @DeleteMapping("/petitions/{petitionId}/release")
    public ResponseEntity<Void> cancelReleasePetition(@PathVariable Long petitionId) {
        petitionService.cancelReleasePetition(petitionId);
        return ResponseEntity.noContent().build();
    }

    @LoginRequired
    @PostMapping("/petitions/{petitionId}/agreements")
    public ResponseEntity<Void> agreePetition(@RequestBody AgreementRequest agreementRequest,
                                              @PathVariable Long petitionId,
                                              @LoginUser SimpleUser simpleUser) {
        petitionService.agree(agreementRequest, petitionId, simpleUser.getId());
        return ResponseEntity.ok().build();
    }
}
