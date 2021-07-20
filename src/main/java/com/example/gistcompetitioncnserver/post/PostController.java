package com.example.gistcompetitioncnserver.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping("")
    public List<Post> retrieveAllPost(){
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Post> retrievePost(@PathVariable Long id){
        return postRepository.findById(id);
    }

    @PostMapping("")
    public ResponseEntity<Post> createUser(@RequestBody Post post){

        Post savedPost = postRepository.save(post);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedPost)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id){
        postRepository.deleteById(id);
    }

//    @PutMapping("/{id}")
//    public void amendPost(@PathVariable Long id, @RequestBody Post post){
//        postRepository.
//    }

}
