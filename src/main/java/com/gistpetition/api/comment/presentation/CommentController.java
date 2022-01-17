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
    @PostMapping("/petitions/{petitionId}/comments")
    public ResponseEntity<Void> createComment(@PathVariable Long petitionId,
                                              @RequestBody CommentRequest commentRequest,
                                              @LoginUser SimpleUser simpleUser) {
        Long commentId = commentService.createComment(petitionId, commentRequest, simpleUser.getId());
        return ResponseEntity.created(URI.create("/petitions/" + petitionId + "/comments/" + commentId)).build();
    }

    @GetMapping("/petitions/{petitionId}/comments")
    public ResponseEntity<Object> getComments(@PathVariable Long petitionId) {
        return ResponseEntity.ok().body(commentService.getCommentsByPetitionId(petitionId));
    }

    @LoginRequired
    @PutMapping("/petitions/{petitionId}/comments/{commentId}")
    public ResponseEntity<Object> updateComment(@PathVariable Long petitionId,
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
    @DeleteMapping("/petitions/{petitionId}/comments/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long petitionId,
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
