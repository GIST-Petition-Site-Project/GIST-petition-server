package com.gistpetition.api.post.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Sort sort, Long userId);

    Page<Post> findByCategory(Category category, Pageable pageable);
    Page<Post> findAll(Pageable pageable);

    Page<Post> findByTitleContains(String keyword,Pageable pageable);

    @Query("SELECT p FROM Post AS p LEFT JOIN FETCH p.agreements WHERE p.id=:postId")
    Post findPostByWithEagerMode(@Param("postId") Long postId);
}
