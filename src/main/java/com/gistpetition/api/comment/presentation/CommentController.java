package com.gistpetition.api.comment.presentation;


import com.gistpetition.api.comment.application.CommentService;
import com.gistpetition.api.comment.dto.CommentRequest;
import com.gistpetition.api.config.annotation.LoginRequired;
import com.gistpetition.api.config.annotation.LoginUser;
import com.gistpetition.api.user.domain.SimpleUser;
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
                                              @LoginUser SimpleUser simpleUser) {
        Long commentId = commentService.createComment(postId, commentRequest, simpleUser.getId());
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
                                                @LoginUser SimpleUser simpleUser) {
        if (simpleUser.hasManagerAuthority()) {
            commentService.updateComment(commentId, updateRequest);
            return ResponseEntity.noContent().build();
        }
        commentService.updateCommentByOwner(simpleUser.getId(), commentId, updateRequest);
        return ResponseEntity.noContent().build();
    }

    @LoginRequired
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @LoginUser SimpleUser simpleUser) {
        if (simpleUser.hasManagerAuthority()) {
            commentService.deleteComment(commentId);
            return ResponseEntity.noContent().build();
        }
        commentService.deleteCommentByOwner(simpleUser.getId(), commentId);
        return ResponseEntity.noContent().build();
    }
}
