package com.example.gistcompetitioncnserver.like;

import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/gistps/api/v1/post")
public class LikeController {

    private final LikeService likeService;
    private final UserService userService;

    @PostMapping("/{id}/like")
    public ResponseEntity<Object> LikePost(@PathVariable Long id, @AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);

        return ResponseEntity
                .ok()
                .body(Boolean.toString(likeService.LikePost(id, user.getId())));
    }


    @GetMapping("/{id}/like")
    public int getNumberofLike(@PathVariable Long id) {
        return likeService.getNumberofLike(id);
    }

    @PostMapping("/{id}/like/check")
    public ResponseEntity<Object> CheckLikePost(@PathVariable Long id, @AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);

        return ResponseEntity
                .ok()
                .body(Boolean.toString(likeService.CheckLikePost(id, user.getId())));
    }

}
