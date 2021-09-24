package com.example.gistcompetitioncnserver.like;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<LikeToPost,Long> {
    public List<LikeToPost> findByUserIdAndPostId(Long userId,Long postId);
    public int countByPostId(Long postId);
}
