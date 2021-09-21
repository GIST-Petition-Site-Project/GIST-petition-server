package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostRepository;
import com.example.gistcompetitioncnserver.post.PostService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@RestController
@RequestMapping("/gistps/api/v1/post")
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    @PostMapping("/{id}/comment")
    public String createComment(@PathVariable Long id, @RequestBody
                                Comment request){

        commentService.createComment(id, request);
        return "done";
    }

    @DeleteMapping("/{id}/comment/{commentId}")
    public String deleteComment(@PathVariable Long id, @PathVariable Long commentId){

        Optional<Post> post = postService.retrievePost(id);
        if(!post.isPresent()){
            return "There are no posts";
        }
        commentService.deleteComment(commentId);
        return "success";
    }

    @GetMapping("/{id}/comment")
    public List<Comment> getComments(@PathVariable Long id) {
        return commentService.getCommentsByPostId(id);
    }

}
