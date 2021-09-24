package com.example.gistcompetitioncnserver.comment;

import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostRepository;
import com.example.gistcompetitioncnserver.post.PostService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;


    @Transactional
    public Long createComment(Long id, CommentRequestDto commentRequestDto, Long userId){
        Post post = postRepository.getById(id);

        Long commentId = commentRepository.save(
                Comment.builder()
                .content(commentRequestDto.getContent())
                .created(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .post(post)
                .userId(userId)
                .build()).getCommentId();

        return commentId;

    }

    public void deleteComment(Long commentId){
        commentRepository.deleteById(commentId);
    }

    public List<Comment> getCommentsByPostId(Long id){ return commentRepository.findByPostId(id);}

    public boolean existCommentId(Long commentId){
        return commentRepository.findByCommentId(commentId).isPresent();
    }

    // Method to check if the real comment writer delete the comment.
    public boolean equalUserToComment(Long postCommnetId, Long writerId){
        return writerId.equals(commentRepository.findUserIdByCommentId(postCommnetId));
    }

}
