package com.example.gistcompetitioncnserver.post;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.exception.ErrorCase;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PostController {

    private final PostService postService;
    private final UserService userService;


    @PostMapping("/posts")
    public ResponseEntity<Void> createPost(@Validated @RequestBody PostRequest postRequest,
                                           @AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);

        if (!isRequestBodyValid(postRequest)) {
            throw new CustomException(ErrorCase.INVAILD_FILED_ERROR);
        }

        return ResponseEntity.created(URI.create("/posts/" + postService.createPost(postRequest, user.getId()))).build();
    }

    private boolean isRequestBodyValid(PostRequest postRequest) {
        return postRequest.getTitle() != null &&
                postRequest.getDescription() != null &&
                postRequest.getCategory() != null;
    }

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> retrieveAllPost() {
        return ResponseEntity.ok().body(postService.retrieveAllPost());
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<Post> retrievePost(@PathVariable Long postId) {
        return ResponseEntity.ok().body(postService.retrievePost(postId));
    }

    @GetMapping("/posts/me")
    public ResponseEntity<List<Post>> retrievePostsByUserId(@AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);

        return ResponseEntity.ok().body(postService.retrievePostsByUserId(user.getId()));
    }

    @GetMapping("/posts/count")
    public ResponseEntity<Long> getPageNumber() {
        return ResponseEntity.ok().body(postService.getPageNumber());
    }

    @GetMapping("/posts/category")
    public ResponseEntity<List<Post>> getPostsByCategory(@RequestParam("categoryName") String categoryName) {
        return ResponseEntity.ok().body(postService.getPostsByCategory(categoryName));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{postId}/agreements")
    public ResponseEntity<Boolean> agreePost(@PathVariable Long postId, @AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);

        return ResponseEntity
                .ok()
                .body(postService.agree(postId, user.getId()));
    }

    @GetMapping("/posts/{postId}/agreements")
    public ResponseEntity<Integer> getNumberOfAgreement(@PathVariable Long postId) {
        return ResponseEntity
                .ok()
                .body(postService.getNumberOfAgreements(postId));
    }

    @GetMapping("/posts/{postId}/agreements/me")
    public ResponseEntity<Boolean> getStateOfAgreement(@PathVariable Long postId, @AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);
        return ResponseEntity
                .ok()
                .body(postService.getStateOfAgreement(postId, user.getId()));
    }
}
