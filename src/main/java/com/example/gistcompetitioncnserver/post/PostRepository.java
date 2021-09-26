package com.example.gistcompetitioncnserver.post;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Sort sort, Long userId);
    List<Post> findByCategory(Sort sort, String categoryName);


    @Query("SELECT distinct p from Post p left join fetch p.comment")
    List<Post> findAllJoinFetch(Sort sort);
}
