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
@RequestMapping("/v1")
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;

    private boolean isRequestBodyValid(CommentRequestDto commentRequestDto) {
        return commentRequestDto.getContent() != null;
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Object> createComment(@PathVariable Long postId, @RequestBody CommentRequestDto
            commentRequestDto, @AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);

        if (!isRequestBodyValid(commentRequestDto)) {
            throw new CustomException(ErrorCase.INVAILD_FILED_ERROR);
        }

        return ResponseEntity
                .created(URI.create("/post/" + postId + "/comment/" + commentService.createComment(postId, commentRequestDto,
                        user.getId())))
                .build();
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Object> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok().body(commentService.getCommentsByPostId(postId));
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long postId, @PathVariable Long commentId,
                                                @AuthenticationPrincipal String email) {

        User user = userService.findUserByEmail2(email);

        Optional<Post> post = postService.retrievePost(postId);
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
}
