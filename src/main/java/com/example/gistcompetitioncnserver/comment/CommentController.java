package com.example.gistcompetitioncnserver.comment;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.example.gistcompetitioncnserver.DataLoader.ADMIN;

@AllArgsConstructor
@RestController
@RequestMapping("/v1")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Void> createComment(@PathVariable Long postId,
                                              @RequestBody CommentRequest commentRequest) {

        Long commentId = commentService.createComment(postId, commentRequest, ADMIN.getId());
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
        commentService.updateComment(ADMIN.getId(), commentId, updateRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long postId,
                                                @PathVariable Long commentId) {
        commentService.deleteComment(ADMIN.getId(), commentId);
        return ResponseEntity.noContent().build();
    }
}
