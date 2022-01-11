package com.example.gistcompetitioncnserver.comment;


import com.example.gistcompetitioncnserver.config.annotation.LoginRequired;
import com.example.gistcompetitioncnserver.config.annotation.LoginUser;
import com.example.gistcompetitioncnserver.user.SessionUser;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping("/v1")
public class CommentController {
    private final CommentService commentService;

    @LoginRequired
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Void> createComment(@PathVariable Long postId,
                                              @RequestBody CommentRequest commentRequest,
                                              @LoginUser SessionUser sessionUser) {
        Long commentId = commentService.createComment(postId, commentRequest, sessionUser.getId());
        return ResponseEntity.created(URI.create("/posts/" + postId + "/comments/" + commentId)).build();
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Object> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok().body(commentService.getCommentsByPostId(postId));
    }

    @LoginRequired
    @PutMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Object> updateComment(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @Validated @RequestBody CommentRequest updateRequest,
                                                @LoginUser SessionUser sessionUser) {
        if (sessionUser.hasManagerAuthority()) {
            commentService.updateComment(commentId, updateRequest);
            return ResponseEntity.noContent().build();
        }
        commentService.updateCommentByOwner(sessionUser.getId(), commentId, updateRequest);
        return ResponseEntity.noContent().build();
    }

    @LoginRequired
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @LoginUser SessionUser sessionUser) {
        if (sessionUser.hasManagerAuthority()) {
            commentService.deleteComment(commentId);
            return ResponseEntity.noContent().build();
        }
        commentService.deleteCommentByOwner(sessionUser.getId(), commentId);
        return ResponseEntity.noContent().build();
    }
}
