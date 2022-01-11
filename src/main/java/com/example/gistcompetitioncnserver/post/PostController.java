package com.example.gistcompetitioncnserver.post;

import com.example.gistcompetitioncnserver.exception.user.UnAuthenticatedException;
import com.example.gistcompetitioncnserver.exception.user.UnAuthorizedUserException;
import com.example.gistcompetitioncnserver.user.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PostController {
    private final PostService postService;
    private final HttpSession httpSession;

    @PostMapping("/posts")
    public ResponseEntity<Void> createPost(@Validated @RequestBody PostRequest postRequest) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (sessionUser == null) {
            throw new UnAuthenticatedException();
        }
        return ResponseEntity.created(URI.create("/posts/" + postService.createPost(postRequest, sessionUser.getId()))).build();
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
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (sessionUser == null) {
            throw new UnAuthenticatedException();
        }
        return ResponseEntity.ok().body(postService.retrievePostsByUserId(sessionUser.getId()));
    }

    @GetMapping("/posts/count")
    public ResponseEntity<Long> getPageNumber() {
        return ResponseEntity.ok().body(postService.getPageNumber());
    }

    @GetMapping("/posts/category")
    public ResponseEntity<List<Post>> getPostsByCategory(@RequestParam("categoryName") String categoryName) {
        return ResponseEntity.ok().body(postService.getPostsByCategory(categoryName));
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable Long postId, @Validated @RequestBody PostRequest changeRequest) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (sessionUser == null) {
            throw new UnAuthenticatedException();
        }
        if (!sessionUser.hasManagerAuthority()) {
            throw new UnAuthorizedUserException();
        }
        postService.updatePostDescription(postId, changeRequest.getDescription());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (sessionUser == null) {
            throw new UnAuthenticatedException();
        }
        if (!sessionUser.hasManagerAuthority()) {
            throw new UnAuthorizedUserException();
        }
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{postId}/agreements")
    public ResponseEntity<Boolean> agreePost(@PathVariable Long postId) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (sessionUser == null) {
            throw new UnAuthenticatedException();
        }
        return ResponseEntity.ok().body(postService.agree(postId, sessionUser.getId()));
    }

    @GetMapping("/posts/{postId}/agreements")
    public ResponseEntity<Integer> getNumberOfAgreement(@PathVariable Long postId) {
        return ResponseEntity.ok().body(postService.getNumberOfAgreements(postId));
    }

    @GetMapping("/posts/{postId}/agreements/me")
    public ResponseEntity<Boolean> getStateOfAgreement(@PathVariable Long postId) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (sessionUser == null) {
            throw new UnAuthenticatedException();
        }
        return ResponseEntity.ok().body(postService.getStateOfAgreement(postId, sessionUser.getId()));
    }
}
