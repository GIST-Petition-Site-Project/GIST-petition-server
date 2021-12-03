package com.example.gistcompetitioncnserver.comment;


import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserService;
import java.net.URI;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/v1")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    @PostMapping("/{postId}/comment")
    public ResponseEntity<Void> createComment(@PathVariable Long postId,
                                              @RequestBody CommentRequest commentRequest,
                                              @AuthenticationPrincipal String email) {

        User user = userService.findUserByEmail2(email);

        Long commentId = commentService.createComment(postId, commentRequest, user.getId());
        return ResponseEntity.created(URI.create("/post/" + postId + "/comment/" + commentId)).build();
    }

    @GetMapping("/{id}/comment")
    public ResponseEntity<Object> getComments(@PathVariable Long id) {
        return ResponseEntity.ok().body(commentService.getCommentsByPostId(id));
    }

    @PutMapping("/{postId}/comment/{commentId}")
    public ResponseEntity<Object> updateComment(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @RequestBody CommentRequest updateRequest,

                                                @AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);

        commentService.updateComment(user.getId(), commentId, updateRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/comment/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);

        commentService.deleteComment(user.getId(), commentId);
        return ResponseEntity.noContent().build();
    }

}
