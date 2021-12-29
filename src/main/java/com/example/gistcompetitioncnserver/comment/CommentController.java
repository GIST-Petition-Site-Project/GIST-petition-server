package com.example.gistcompetitioncnserver.comment;


import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping("/v1")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Void> createComment(@PathVariable Long postId,
                                              @Validated @RequestBody CommentRequest commentRequest,
                                              @AuthenticationPrincipal String email) {

        User user = userService.findUserByEmail2(email);

        Long commentId = commentService.createComment(postId, commentRequest, user.getId());
        return ResponseEntity.created(URI.create("/posts/" + postId + "/comments/" + commentId)).build();
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Object> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok().body(commentService.getCommentsByPostId(postId));
    }

    @PutMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Object> updateComment(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @Validated @RequestBody CommentRequest updateRequest,
                                                @AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);

        commentService.updateComment(user.getId(), commentId, updateRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);

        commentService.deleteComment(user.getId(), commentId);
        return ResponseEntity.noContent().build();
    }

}
