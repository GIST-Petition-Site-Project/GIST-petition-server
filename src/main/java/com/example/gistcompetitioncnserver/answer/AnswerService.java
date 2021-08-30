package com.example.gistcompetitioncnserver.post;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;


    public Post createPost(Post post){
        post.setCreated(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return postRepository.save(post);
    }

    public List<Post> retrieveAllPost(){
        return postRepository.findAll();
    }

    public List<Post> retrievePostsByUserId(Long user_id){
        return postRepository.findByUserId(user_id);
    }

    public Optional<Post> retrievePost(Long id){
        return postRepository.findById(id);
    }

    public Long getPageNumber(){
        return postRepository.count();
    }

    public List<Post> getPostsByCategory(String categoryName){
        return postRepository.findByCategory(categoryName);
    }

    @Transactional
    public void deletePost(Long id){
        postRepository.deleteById(id);
    }






}
