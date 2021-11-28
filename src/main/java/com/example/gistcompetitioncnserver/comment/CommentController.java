package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.common.ErrorCase;
import com.example.gistcompetitioncnserver.common.ErrorMessage;
import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostService;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserService;
import java.net.URI;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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

    private boolean isRequestBodyValid(CommentRequestDto commentRequestDto) {
        return commentRequestDto.getContent() != null;
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long id, @RequestBody CommentRequestDto
            commentRequestDto, @AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);

        if (!isRequestBodyValid(commentRequestDto)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.INVAILD_FILED_ERROR));
        }

        return ResponseEntity
                .created(URI.create("/post/" + id + "/comment/" + commentService.createComment(id, commentRequestDto,
                        user.getId())))
                .build();
    }

    @DeleteMapping("/{id}/comment/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long id, @PathVariable Long commentId,
                                                @AuthenticationPrincipal String email) {

        User user = userService.findUserByEmail2(email);

        Optional<Post> post = postService.retrievePost(id);
        if (post.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_POST_ERROR));
        }

        if (!commentService.existCommentId(commentId)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_COMMENT_ERROR));
        }

        if (!commentService.equalUserToComment(commentId, user.getId())) {
            return ResponseEntity.badRequest().body(
                    new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.FORBIDDEN_ERROR)
            );
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/comment")
    public ResponseEntity<Object> getComments(@PathVariable Long id) {
        return ResponseEntity.ok().body(commentService.getCommentsByPostId(id));
    }

}
