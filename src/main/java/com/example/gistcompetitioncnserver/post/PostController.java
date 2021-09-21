package com.example.gistcompetitioncnserver.post;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequiredArgsConstructor
@RequestMapping("/gistps/api/v1") // not used in rest api like v1 url
public class PostController {

    private final PostService postService;

    //게시글 작성 요청 보냈을 때 정상적으로 게시글이 생성되면 리턴값으로 작성된 게시글 고유 id 반환해주기
    @PostMapping("/post")
    public ResponseEntity<Object> createPost(@RequestBody PostRequestDto postRequestDto){
        return ResponseEntity.created(URI.create("/post/" + postService.createPost(postRequestDto))).build();
    }

    @GetMapping("/post")
    public ResponseEntity<Object> retrieveAllPost(Pageable pageable){
        Page<Post> posts = postService.retrieveAllPost(pageable);
        return ResponseEntity.ok().body(posts);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<Object> retrievePost(@PathVariable Long id){
        return ResponseEntity.ok().body(postService.retrievePost(id));
    }

    @GetMapping("/post/count")
    public ResponseEntity<Object> getPageNumber(){
        return ResponseEntity.ok().body(postService.getPageNumber());
    }

    @GetMapping("/post/category")
    public ResponseEntity<Object> getPostsByCategory(@RequestParam("categoryName") String categoryName){
        return ResponseEntity.ok().body(postService.getPostsByCategory(categoryName));
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<Object> deletePost(@PathVariable Long id){
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }


//    @PutMapping("/{id}")
//    public void amendPost(@PathVariable Long id, @RequestBody Post post){
//        postRepository.
//    }

}
