package com.gistpetition.api.answer.presentation;

import com.gistpetition.api.answer.application.AnswerService;
import com.gistpetition.api.answer.domain.Answer;
import com.gistpetition.api.answer.dto.AnswerRequest;
import com.gistpetition.api.answer.dto.AnswerRevisionResponse;
import com.gistpetition.api.config.annotation.AdminPermissionRequired;
import com.gistpetition.api.config.annotation.ManagerPermissionRequired;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/v1")
public class AnswerController {

    private final AnswerService answerService;

    @ManagerPermissionRequired
    @PostMapping("/petitions/{petitionId}/answer")
    public ResponseEntity<Object> createAnswer(@PathVariable Long petitionId,
                                               @Validated @RequestBody AnswerRequest answerRequest) {
        Long answerId = answerService.createAnswer(petitionId, answerRequest);
        return ResponseEntity.created(URI.create("/v1/petitions/" + petitionId + "/answer/" + answerId)).build();
    }

    @GetMapping("/petitions/{petitionId}/answer")
    public ResponseEntity<Answer> retrieveAnswer(@PathVariable Long petitionId) {
        return ResponseEntity.ok().body(answerService.retrieveAnswerByPetitionId(petitionId));
    }

    @GetMapping("/answers/count")
    public ResponseEntity<Long> getNumberOfAnswers() {
        return ResponseEntity.ok().body(answerService.getNumberOfAnswers());
    }

    @ManagerPermissionRequired
    @GetMapping("/answers/{answerId}/revisions")
    public ResponseEntity<Page<AnswerRevisionResponse>> retrieveAnswerRevisions(@PathVariable Long answerId, Pageable pageable) {
        return ResponseEntity.ok().body(answerService.retrieveRevisionsOfAnswer(answerId, pageable));
    }

    @ManagerPermissionRequired
    @PutMapping("/petitions/{petitionId}/answer")
    public ResponseEntity<Void> updateAnswer(@PathVariable Long petitionId,
                                             @Validated @RequestBody AnswerRequest changeRequest) {
        answerService.updateAnswer(petitionId, changeRequest);
        return ResponseEntity.ok().build();
    }

    @ManagerPermissionRequired
    @DeleteMapping("/petitions/{petitionId}/answer")
    public ResponseEntity<Object> deleteAnswer(@PathVariable Long petitionId) {
        answerService.deleteAnswer(petitionId);
        return ResponseEntity.noContent().build();
    }
}
