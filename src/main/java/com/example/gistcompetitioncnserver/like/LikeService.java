package com.example.gistcompetitioncnserver.like;


import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    public boolean LikePost(Long id, LikeToPost request) {

        Long userId = request.getUserId();
        Post post = postRepository.getById(id);
        List<LikeToPost> likes = likeRepository.findByUserIdAndPostId(userId, post.getId());

        if (likes.isEmpty()) {
            request.setPost(post);
            likeRepository.save(request);
            post.setAccepted(getNumberofLike(id));
            postRepository.save(post);
            return true;
        }

        for (LikeToPost like : likes) {
            likeRepository.delete(like);
            post.setAccepted(getNumberofLike(id));
            postRepository.save(post);
        }
        return false;
    }

    public boolean CheckLikePost(Long id, LikeToPost request) {

        Long userId = request.getUserId();
        Post post = postRepository.getById(id);
        List<LikeToPost> likes = likeRepository.findByUserIdAndPostId(userId, post.getId());

        if (likes.isEmpty()) {
            return false;
        }
        return true;
    }

    public int getNumberofLike(Long id) {
        Post post = postRepository.getById(id);
        return likeRepository.countByPostId(id);
    }
}
