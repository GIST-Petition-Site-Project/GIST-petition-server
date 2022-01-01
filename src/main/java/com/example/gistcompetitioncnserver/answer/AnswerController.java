package com.example.gistcompetitioncnserver.answer;

import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/v1")
public class AnswerController {
    private final static User user = new User(1L, "email@email.com", "password", UserRole.USER, true);

    private final AnswerService answerService;

    @PostMapping("/posts/{postId}/answer")
    public ResponseEntity<Object> createAnswer(@PathVariable Long postId,
                                               @Validated @RequestBody AnswerRequest answerRequest) {
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
                                             @Validated @RequestBody AnswerRequest changeRequest) {
        answerService.updateAnswer(postId, user.getId(), changeRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{postId}/answer")
    public ResponseEntity<Object> deleteComment(@PathVariable Long postId) {
        answerService.deleteAnswer(user.getId(), postId);
        return ResponseEntity.noContent().build();
    }
}
