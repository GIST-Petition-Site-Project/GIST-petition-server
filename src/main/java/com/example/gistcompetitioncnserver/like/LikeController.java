package com.example.gistcompetitioncnserver.like;

import com.example.gistcompetitioncnserver.common.ErrorCase;
import com.example.gistcompetitioncnserver.common.ErrorMessage;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/gistps/api/v1/post")
public class LikeController {

    private final LikeService likeService;
    private final UserService userService;

    @PostMapping("/{id}/like")
    public ResponseEntity<Object> LikePost(@PathVariable Long id, @AuthenticationPrincipal String email) {
        Optional<User> user = userService.findUserByEmail(email);

        if (user.isEmpty()){
            return ResponseEntity.badRequest().body(
                    new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_USER_ERROR)
            );
        }

        if(!user.get().isEnabled()){
            return ResponseEntity.badRequest().body(
                    new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_VERIFICATION_EMAIL_ERROR)
            );
        }


        return ResponseEntity
                .ok()
                .body(Boolean.toString(likeService.LikePost(id, user.get().getId())));
    }


    @GetMapping("/{id}/like")
    public int getNumberofLike(@PathVariable Long id) {
        return likeService.getNumberofLike(id);
    }

    @PostMapping("/{id}/like/check")
    public boolean CheckLikePost(@PathVariable Long id, @RequestBody LikeToPost LikeToPost) {
        return likeService.CheckLikePost(id, LikeToPost);
    }

}
