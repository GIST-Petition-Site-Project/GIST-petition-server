package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/posts")
public class CommentController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

//    @GetMapping("/{id}/comment")
//    public List<Comment> retrieveAllComment(@PathVariable Long id){
//
//    }

    @PostMapping("/{id}/comment")
    public String retrieveComment(@PathVariable Long id, @PathVariable Long comment_id, @RequestBody Comment comment){

        Optional<Post> post = postRepository.findById(id);
        comment.setPost(post.get());
        commentRepository.save(comment);

        return "done";

    }


}
