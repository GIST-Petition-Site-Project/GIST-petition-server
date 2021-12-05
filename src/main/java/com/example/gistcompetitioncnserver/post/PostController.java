package com.example.gistcompetitioncnserver.post;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.exception.ErrorCase;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PostController {

    private final PostService postService;
    private final UserService userService;

    private boolean isRequestBodyValid(PostRequestDto postRequestDto) {
        return postRequestDto.getTitle() != null &&
                postRequestDto.getDescription() != null &&
                postRequestDto.getCategory() != null;
    }

    @PostMapping("/posts")
    public ResponseEntity<Object> createPost(@RequestBody PostRequestDto postRequestDto,
                                             @AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);

        if (!isRequestBodyValid(postRequestDto)) {
            throw new CustomException(ErrorCase.INVAILD_FILED_ERROR);
        }

        return ResponseEntity.created(URI.create("/posts/" + postService.createPost(postRequestDto, user.getId())))
                .build();
    }

    @GetMapping("/posts")
    public ResponseEntity<Object> retrieveAllPost() {
        return ResponseEntity.ok().body(postService.retrieveAllPost());
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<Object> retrievePost(@PathVariable Long postId) {
        return ResponseEntity.ok().body(postService.retrievePost(postId));
    }

    @GetMapping("/posts/me")
    public ResponseEntity<Object> retrievePostsByUserId(@AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);

        return ResponseEntity.ok().body(postService.retrievePostsByUserId(user.getId()));
    }

    @GetMapping("/posts/count")
    public ResponseEntity<Object> getPageNumber() {
        return ResponseEntity.ok().body(postService.getPageNumber());
    }

    @GetMapping("/posts/category")
    public ResponseEntity<Object> getPostsByCategory(@RequestParam("categoryName") String categoryName) {
        return ResponseEntity.ok().body(postService.getPostsByCategory(categoryName));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Object> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{postId}/agreements")
    public ResponseEntity<Object> agreePost(@PathVariable Long postId, @AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);

        return ResponseEntity
                .ok()
                .body(postService.agree(postId, user.getId()).toString());
    }

    @GetMapping("/posts/{postId}/agreements")
    public ResponseEntity<Object> getNumberOfAgreement(@PathVariable Long postId) {
        return ResponseEntity
                .ok()
                .body(postService.getNumberOfAgreements(postId));
    }
    @GetMapping("/posts/{postId}/agreements/me")
    public ResponseEntity<Object> getStateOfAgreement(@PathVariable Long postId,@AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);
        return ResponseEntity
                .ok()
                .body(postService.getStateOfAgreement(postId,user.getId()));
    }
}
