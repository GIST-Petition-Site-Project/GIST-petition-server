package com.example.gistcompetitioncnserver.post;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @PostMapping("")
    public ResponseEntity<Post> createUser(@RequestBody Post post){
        Post savedPost = postService.createPost(post);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedPost)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("")
    public List<Post> retrieveAllPost(){
        return postService.retrieveAllPost();
    }

    @GetMapping("/{id}")
    public Optional<Post> retrievePost(@PathVariable Long id){
        return postService.retrievePost(id);
    }

    @GetMapping("/count")
    public Long getPageNumber(){
        return postService.getPageNumber();
    }

//    @GetMapping("/list")
//    public List


    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id){
        postService.deletePost(id);
    }



//    @PutMapping("/{id}")
//    public void amendPost(@PathVariable Long id, @RequestBody Post post){
//        postRepository.
//    }

}
