package com.example.gistcompetitioncnserver.post;

import com.example.gistcompetitioncnserver.common.ErrorCase;
import com.example.gistcompetitioncnserver.common.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@RequiredArgsConstructor
@RequestMapping("/gistps/api/v1") // not used in rest api like v1 url
public class PostController {

    private final PostService postService;

    private boolean isRequestBodyValid(PostRequestDto postRequestDto){
        return postRequestDto.getUserId() != null &&
                postRequestDto.getTitle() != null &&
                postRequestDto.getDescription() != null &&
                postRequestDto.getCategory() != null ;
    }

    //게시글 작성 요청 보냈을 때 정상적으로 게시글이 생성되면 리턴값으로 작성된 게시글 고유 id 반환해주기
    @PostMapping("/post")
    public ResponseEntity<Object> createPost(@RequestBody PostRequestDto postRequestDto){

        if (!isRequestBodyValid(postRequestDto)){
            return ResponseEntity.badRequest().body(
                    new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.INVAILD_FILED_ERROR)
            );
        }

        return ResponseEntity.created(URI.create("/post/" + postService.createPost(postRequestDto))).build();
    }

    @GetMapping("/post")
    public ResponseEntity<Object> retrieveAllPost(){
        return ResponseEntity.ok().body(postService.retrieveAllPost());
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


}
