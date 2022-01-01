package com.example.gistcompetitioncnserver.answer;

import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/v1")
public class AnswerController {

    private final AnswerService answerService;
    private final UserService userService;

    @PostMapping("/posts/{postId}/answer")
    public ResponseEntity<Object> createAnswer(@PathVariable Long postId,
                                               @Validated @RequestBody AnswerRequest answerRequest,
                                               @AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);
        Long answerId = answerService.createAnswer(postId, answerRequest, user.getId());
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

    @PutMapping("/posts/{postId}/answer")
    public ResponseEntity<Void> updateAnswer(@PathVariable Long postId,
                                             @Validated @RequestBody AnswerRequest changeRequest,
                                             @AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);
        answerService.updateAnswer(postId, user.getId(), changeRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{postId}/answer")
    public ResponseEntity<Object> deleteComment(@PathVariable Long postId,
                                                @AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);

        answerService.deleteAnswer(user.getId(), postId);
        return ResponseEntity.noContent().build();
    }
}
