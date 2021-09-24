package com.example.gistcompetitioncnserver.like;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/gistps/api/v1/post")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{id}/like")
    public boolean LikePost(@PathVariable Long id, @RequestBody LikeToPost LikeToPost) {
        return likeService.LikePost(id, LikeToPost);
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
