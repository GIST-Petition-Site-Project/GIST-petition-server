package com.example.gistcompetitioncnserver.post;


import com.example.gistcompetitioncnserver.comment.Comment;
import com.example.gistcompetitioncnserver.comment.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Long createPost(PostRequestDto postRequestDto, Long userId){
        Long result = postRepository.save(
                Post.builder()
                .title(postRequestDto.getTitle())
                .description(postRequestDto.getDescription())
                .category(postRequestDto.getCategory())
                .userId(userId)
                .created(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .build()
        ).getId();

        return result;
    }

//    public List<Post> retrieveAllPost(){
//        return postRepository.findAllJoinFetch(Sort.by(Sort.Direction.DESC, "id"));
//    }
    public List<Post> retrieveAllPost(){
        return postRepository.findAllJoinFetch(Sort.by(Sort.Direction.DESC, "id"));
    }

    public List<Post> retrievePostsByUserId(Long user_id){
        return postRepository.findByUserId(Sort.by(Sort.Direction.DESC, "id"),user_id);
    }

    public Optional<Post> retrievePost(Long id){
        return postRepository.findById(id);
    }

    public Long getPageNumber(){
        return postRepository.count();
    }

    public List<Post> getPostsByCategory(String categoryName){
        return postRepository.findByCategory(Sort.by(Sort.Direction.DESC, "id"),categoryName);
    }

    public void updateAnsweredPost(Long id){
        Post post = postRepository.getById(id);
        post.setAnswered(true);
        postRepository.save(post);
    }

    @Transactional // like도 지워야함
    public void deletePost(Long id){
        List<Long> commentIds = new ArrayList<>();

        for(Comment comment : commentRepository.findByPostId(id)){
            commentIds.add(comment.getCommentId());
        }

        if(!commentIds.isEmpty()){
            commentRepository.deleteAllByPostIdInQuery(commentIds);
        }
        postRepository.deleteById(id);
    }






}
