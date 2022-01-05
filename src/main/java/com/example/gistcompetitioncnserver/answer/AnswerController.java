package com.example.gistcompetitioncnserver.answer;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.user.SessionUser;
import com.example.gistcompetitioncnserver.user.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/v1")
public class AnswerController {

    private final AnswerService answerService;
    private final HttpSession httpSession;

    @PostMapping("/posts/{postId}/answer")
    public ResponseEntity<Object> createAnswer(@PathVariable Long postId,
                                               @Validated @RequestBody AnswerRequest answerRequest) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (!sessionUser.getEnabled()) {
            throw new CustomException("이메일 인증이 필요합니다!");
        }
        if (!sessionUser.hasManagerAuthority()) {
            throw new CustomException("답변 권한이 없습니다.");
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

    @PutMapping("/posts/{postId}/answer")
    public ResponseEntity<Void> updateAnswer(@PathVariable Long postId,
                                             @Validated @RequestBody AnswerRequest changeRequest) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (!sessionUser.getEnabled()) {
            throw new CustomException("이메일 인증이 필요합니다!");
        }
        if (!sessionUser.hasManagerAuthority()) {
            throw new CustomException("답변 수정 권한이 없습니다.");
        }
        answerService.updateAnswer(postId, changeRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{postId}/answer")
    public ResponseEntity<Object> deleteComment(@PathVariable Long postId) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (!sessionUser.getEnabled()) {
            throw new CustomException("이메일 인증이 필요합니다!");
        }
        if (!sessionUser.hasManagerAuthority()) {
            throw new CustomException("답변 삭제 권한이 없습니다.");
        }
        answerService.deleteAnswer(postId);
        return ResponseEntity.noContent().build();
    }
}
