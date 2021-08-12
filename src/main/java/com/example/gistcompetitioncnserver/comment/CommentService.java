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
    public void createComment(Long id, Comment request){
        Post post = postRepository.getById(id);
        request.setCreated(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        request.setPost(post);
        commentRepository.save(request);

        //
//        Comment comment = commentRepository.save(
//                Comment.builder()
//                .content(request.getContent())
//                .created(LocalDateTime.now())
//                .post(post)
//                .build());
//
//        // return response

    }

    public void deleteComment(Long commentId){
        commentRepository.deleteById(commentId);
    }

    public List<Comment> getCommentsByPostId(Long id){ return commentRepository.findByPostId(id);}


}
