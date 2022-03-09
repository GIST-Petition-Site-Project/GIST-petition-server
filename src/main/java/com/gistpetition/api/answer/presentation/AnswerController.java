package com.gistpetition.api.answer.presentation;

import com.gistpetition.api.answer.application.AnswerService;
import com.gistpetition.api.answer.domain.Answer;
import com.gistpetition.api.answer.dto.AnswerRevisionResponse;
import com.gistpetition.api.config.annotation.ManagerPermissionRequired;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/v1")
public class AnswerController {

    private final AnswerService answerService;

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
}
