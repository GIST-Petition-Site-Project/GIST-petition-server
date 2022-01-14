package com.gistpetition.api.post.dto;

import com.gistpetition.api.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PostResponse {
    private final String title;
    private final String description;
    private final String categoryName;

    public PostResponse(String title, String description, String categoryName) {
        this.title = title;
        this.description = description;
        this.categoryName = categoryName;
    }

    public static Page<PostResponse> pageOf(Page<Post> page) {
        List<PostResponse> postResponseList = page.getContent().stream().map(PostResponse::of).collect(Collectors.toList());
        return new PageImpl<>(postResponseList, page.getPageable(), page.getTotalElements());
    }

    public static PostResponse of(Post post) {
        return new PostResponse(post.getTitle(), post.getDescription(), post.getCategory().getName());
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategoryName() {
        return categoryName;
    }

}
