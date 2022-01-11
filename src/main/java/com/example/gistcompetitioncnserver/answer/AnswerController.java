package com.example.gistcompetitioncnserver.answer;

import com.example.gistcompetitioncnserver.config.annotation.LoginRequired;
import com.example.gistcompetitioncnserver.config.annotation.LoginUser;
import com.example.gistcompetitioncnserver.exception.user.UnAuthorizedUserException;
import com.example.gistcompetitioncnserver.user.SessionUser;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/v1")
public class AnswerController {

    private final AnswerService answerService;

    @LoginRequired
    @PostMapping("/posts/{postId}/answer")
    public ResponseEntity<Object> createAnswer(@PathVariable Long postId,
                                               @Validated @RequestBody AnswerRequest answerRequest,
                                               @LoginUser SessionUser sessionUser) {
        if (!sessionUser.hasManagerAuthority()) {
            throw new UnAuthorizedUserException();
        }
        Long answerId = answerService.createAnswer(postId, answerRequest, sessionUser.getId());
        return ResponseEntity.created(URI.create("/posts/" + postId + "/answer/" + answerId)).build();
    }

    @GetMapping("/posts/{postId}/answer")
    public ResponseEntity<Answer> retrieveAnswer(@PathVariable Long postId) {
        return ResponseEntity.ok().body(answerService.retrieveAnswerByPostId(postId));
    }

    @GetMapping("/answers/count")
    public ResponseEntity<Long> getNumberOfAnswers() {
        return ResponseEntity.ok().body(answerService.getNumberOfAnswers());
    }

    @LoginRequired
    @PutMapping("/posts/{postId}/answer")
    public ResponseEntity<Void> updateAnswer(@PathVariable Long postId,
                                             @Validated @RequestBody AnswerRequest changeRequest,
                                             @LoginUser SessionUser sessionUser) {
        if (!sessionUser.hasManagerAuthority()) {
            throw new UnAuthorizedUserException();
        }
        answerService.updateAnswer(postId, changeRequest);
        return ResponseEntity.ok().build();
    }

    @LoginRequired
    @DeleteMapping("/posts/{postId}/answer")
    public ResponseEntity<Object> deleteComment(@PathVariable Long postId,
                                                @LoginUser SessionUser sessionUser) {
        if (!sessionUser.hasManagerAuthority()) {
            throw new UnAuthorizedUserException();
        }
        answerService.deleteAnswer(postId);
        return ResponseEntity.noContent().build();
    }
}
