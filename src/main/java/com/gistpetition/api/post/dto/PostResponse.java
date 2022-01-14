package com.gistpetition.api.post.dto;

import com.gistpetition.api.post.domain.Post;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class PostResponse {
    private final String title;
    private final String description;
    private final String categoryName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static Page<PostResponse> pageOf(Page<Post> page) {
        List<PostResponse> postResponseList = page.getContent().stream().map(PostResponse::of).collect(Collectors.toList());
        return new PageImpl<>(postResponseList, page.getPageable(), page.getTotalElements());
    }

    public static PostResponse of(Post post) {
        return new PostResponse(
                post.getTitle(),
                post.getDescription(),
                post.getCategory().getName(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
