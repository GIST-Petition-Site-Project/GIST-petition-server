package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.common.ErrorCase;
import com.example.gistcompetitioncnserver.common.ErrorMessage;
import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@RestController
@RequestMapping("/gistps/api/v1/post")
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    private boolean isRequestBodyValid(CommentRequestDto commentRequestDto){
        return commentRequestDto.getUserId() != null &&
                commentRequestDto.getContent() != null;
    }
    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long id, @RequestBody CommentRequestDto
                                 commentRequestDto){

        if(!isRequestBodyValid(commentRequestDto)){
            return ResponseEntity.badRequest().body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.INVAILD_FILED_ERROR));
        }

        return ResponseEntity
                .created(URI.create("/post/" + id + "/comment/" + commentService.createComment(id, commentRequestDto)))
                .build();
    }

    @DeleteMapping("/{id}/comment/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long id, @PathVariable Long commentId){

        Optional<Post> post = postService.retrievePost(id);
        if(post.isEmpty()){
            return ResponseEntity.badRequest().body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_POST_ERROR));
        }

        if(!commentService.existCommentId(commentId)){
            return ResponseEntity.badRequest().body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_COMMENT_ERROR));
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/comment")
    public ResponseEntity<Object> getComments(@PathVariable Long id) {
        return ResponseEntity.ok().body(commentService.getCommentsByPostId(id));
    }

}
