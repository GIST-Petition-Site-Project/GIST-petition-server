package com.example.gistcompetitioncnserver.comment;


import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.user.SessionUser;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping("/v1")
public class CommentController {
    private final CommentService commentService;
    private final HttpSession httpSession;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Void> createComment(@PathVariable Long postId,
                                              @RequestBody CommentRequest commentRequest) {

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (!sessionUser.getEnabled()) {
            throw new CustomException("이메일 인증이 필요합니다!");
        }
        Long commentId = commentService.createComment(postId, commentRequest, sessionUser.getId());
        return ResponseEntity.created(URI.create("/posts/" + postId + "/comments/" + commentId)).build();
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Object> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok().body(commentService.getCommentsByPostId(postId));
    }

    @PutMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Object> updateComment(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @Validated @RequestBody CommentRequest updateRequest) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (!sessionUser.getEnabled()) {
            throw new CustomException("이메일 인증이 필요합니다!");
        }
        commentService.updateComment(sessionUser.getId(), commentId, updateRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long postId,
                                                @PathVariable Long commentId) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (!sessionUser.getEnabled()) {
            throw new CustomException("이메일 인증이 필요합니다!");
        }
        commentService.deleteComment(sessionUser.getId(), commentId);
        return ResponseEntity.noContent().build();
    }
}
