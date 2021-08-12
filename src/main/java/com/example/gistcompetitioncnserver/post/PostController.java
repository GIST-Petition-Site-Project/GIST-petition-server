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
@RequestMapping("/gistps/api/v1/post")
public class PostController {

    private final PostService postService;

//    @PostMapping("")
//    public ResponseEntity<Post> createPost(@RequestBody Post post){
//        Post savedPost = postService.createPost(post);
//
//        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(savedPost)
//                .toUri();
//
//        return ResponseEntity.created(location).build();
//    }

    //게시글 작성 요청 보냈을 때 정상적으로 게시글이 생성되면 리턴값으로 작성된 게시글 고유 id 반환해주기
    @PostMapping("")
    public Long createPost(@RequestBody Post post){
        Post savedPost = postService.createPost(post);


        return savedPost.getId();
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

    @GetMapping("/category")
    public List<Post> getPostsByCategory(@RequestParam("categoryName") String categoryName){
        return postService.getPostsByCategory(categoryName);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id){
        postService.deletePost(id);
    }



//    @PutMapping("/{id}")
//    public void amendPost(@PathVariable Long id, @RequestBody Post post){
//        postRepository.
//    }

}
