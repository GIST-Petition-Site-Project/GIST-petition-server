package com.gistpetition.api.post.presentation;

import com.gistpetition.api.config.annotation.LoginRequired;
import com.gistpetition.api.config.annotation.LoginUser;
import com.gistpetition.api.config.annotation.ManagerPermissionRequired;
import com.gistpetition.api.post.application.PostService;
import com.gistpetition.api.post.domain.Post;
import com.gistpetition.api.post.dto.PostRequest;
import com.gistpetition.api.post.dto.PostResponse;
import com.gistpetition.api.user.domain.SimpleUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<Page<PostResponse>> retrievePost(@RequestParam(defaultValue = "0") Long categoryId,
                                                           @PageableDefault(sort="createdAt",direction = Sort.Direction.DESC) Pageable pageable) {
        if (categoryId.equals(0L)) {
            return ResponseEntity.ok().body(postService.retrievePost(pageable));
        }
        return ResponseEntity.ok().body(postService.retrievePostByCategoryId(categoryId, pageable));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> retrievePost(@PathVariable Long postId) {
        return ResponseEntity.ok().body(postService.retrievePostById(postId));
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
