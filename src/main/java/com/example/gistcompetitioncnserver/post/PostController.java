package com.example.gistcompetitioncnserver.post;

import com.example.gistcompetitioncnserver.config.annotation.LoginRequired;
import com.example.gistcompetitioncnserver.config.annotation.LoginUser;
import com.example.gistcompetitioncnserver.config.annotation.ManagerPermissionRequired;
import com.example.gistcompetitioncnserver.user.SimpleUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PostController {
    private final PostService postService;

    @LoginRequired
    @PostMapping("/posts")
    public ResponseEntity<Void> createPost(@Validated @RequestBody PostRequest postRequest,
                                           @LoginUser SimpleUser simpleUser) {
        return ResponseEntity.created(URI.create("/posts/" + postService.createPost(postRequest, simpleUser.getId()))).build();
    }

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> retrieveAllPost() {
        return ResponseEntity.ok().body(postService.retrieveAllPost());
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<Post> retrievePost(@PathVariable Long postId) {
        return ResponseEntity.ok().body(postService.retrievePost(postId));
    }

    @LoginRequired
    @GetMapping("/posts/me")
    public ResponseEntity<List<Post>> retrievePostsByUserId(@LoginUser SimpleUser simpleUser) {
        return ResponseEntity.ok().body(postService.retrievePostsByUserId(simpleUser.getId()));
    }

    @GetMapping("/posts/count")
    public ResponseEntity<Long> getPostCount() {
        return ResponseEntity.ok().body(postService.getPostCount());
    }

    @GetMapping("/posts/category")
    public ResponseEntity<List<Post>> getPostsByCategory(@RequestParam("categoryName") String categoryName) {
        return ResponseEntity.ok().body(postService.getPostsByCategory(categoryName));
    }

    @ManagerPermissionRequired
    @PutMapping("/posts/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable Long postId,
                                           @Validated @RequestBody PostRequest changeRequest,
                                           @LoginUser SimpleUser simpleUser) {
        postService.updatePostDescription(postId, changeRequest.getDescription());
        return ResponseEntity.noContent().build();
    }

    @ManagerPermissionRequired
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId,
                                           @LoginUser SimpleUser simpleUser) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @LoginRequired
    @PostMapping("/posts/{postId}/agreements")
    public ResponseEntity<Boolean> agreePost(@PathVariable Long postId,
                                             @LoginUser SimpleUser simpleUser) {
        return ResponseEntity.ok().body(postService.agree(postId, simpleUser.getId()));
    }

    @GetMapping("/posts/{postId}/agreements")
    public ResponseEntity<Integer> getNumberOfAgreement(@PathVariable Long postId) {
        return ResponseEntity.ok().body(postService.getNumberOfAgreements(postId));
    }

    @LoginRequired
    @GetMapping("/posts/{postId}/agreements/me")
    public ResponseEntity<Boolean> getStateOfAgreement(@PathVariable Long postId,
                                                       @LoginUser SimpleUser simpleUser) {
        return ResponseEntity.ok().body(postService.getStateOfAgreement(postId, simpleUser.getId()));
    }
}
