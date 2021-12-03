package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.exception.ErrorCase;
import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostService;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserService;
import java.net.URI;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/gistps/api/v1/post")
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;

    @PostMapping("/{postId}/comment")
    public ResponseEntity<Void> createComment(@PathVariable Long postId,
                                              @RequestBody CommentRequest commentRequest,
                                              @AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);

        Long commentId = commentService.createComment(postId, commentRequest, user.getId());
        return ResponseEntity.created(URI.create("/post/" + postId + "/comment/" + commentId)).build();
    }

    @DeleteMapping("/{id}/comment/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long id,
                                                @PathVariable Long commentId,
                                                @AuthenticationPrincipal String email) {

        User user = userService.findUserByEmail2(email);

        Optional<Post> post = postService.retrievePost(id);
        if (post.isEmpty()) {
            throw new CustomException(ErrorCase.NO_SUCH_POST_ERROR);
        }

        if (!commentService.existCommentId(commentId)) {
            throw new CustomException(ErrorCase.NO_SUCH_COMMENT_ERROR);
        }

        if (!commentService.equalUserToComment(commentId, user.getId())) {
            throw new CustomException(ErrorCase.FORBIDDEN_ERROR);
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/comment")
    public ResponseEntity<Object> getComments(@PathVariable Long id) {
        return ResponseEntity.ok().body(commentService.getCommentsByPostId(id));
    }
}
