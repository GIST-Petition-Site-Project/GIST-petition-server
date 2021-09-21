package com.example.gistcompetitioncnserver.like;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/gistps/api/v1/post")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{id}/like")
    public boolean LikePost(@PathVariable Long id, @RequestBody LikeForPost likeforPost) {
        return likeService.LikePost(id, likeforPost);
    }


    @GetMapping("/{id}/like")
    public int getNumberofLike(@PathVariable Long id) {
        return likeService.getNumberofLike(id);
    }

}
