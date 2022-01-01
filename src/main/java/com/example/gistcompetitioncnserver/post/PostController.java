package com.example.gistcompetitioncnserver.post;

import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRole;
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
    private final static User user = new User(1L, "email@email.com", "password", UserRole.USER, true);

    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<Void> createPost(@Validated @RequestBody PostRequest postRequest) {
        return ResponseEntity.created(URI.create("/posts/" + postService.createPost(postRequest, user.getId()))).build();
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
    public ResponseEntity<List<Post>> retrievePostsByUserId() {
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
    public ResponseEntity<Boolean> agreePost(@PathVariable Long postId) {
        return ResponseEntity.ok().body(postService.agree(postId, user.getId()));
    }

    @GetMapping("/posts/{postId}/agreements")
    public ResponseEntity<Integer> getNumberOfAgreement(@PathVariable Long postId) {
        return ResponseEntity.ok().body(postService.getNumberOfAgreements(postId));
    }

    @GetMapping("/posts/{postId}/agreements/me")
    public ResponseEntity<Boolean> getStateOfAgreement(@PathVariable Long postId) {
        return ResponseEntity.ok().body(postService.getStateOfAgreement(postId, user.getId()));
    }
}
