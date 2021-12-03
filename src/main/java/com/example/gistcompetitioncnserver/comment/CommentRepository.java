package com.example.gistcompetitioncnserver.comment;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);

    @Query("select c.userId from Comment c where c.id = :id")
    Long findUserIdById(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("delete from Comment c where c.id in :ids")
    void deleteAllByPostIdInQuery(@Param("ids") List<Long> ids);
}
