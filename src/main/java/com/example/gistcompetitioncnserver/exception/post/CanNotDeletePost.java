package com.example.gistcompetitioncnserver.exception.post;

import com.example.gistcompetitioncnserver.exception.post.PostException;
import org.springframework.http.HttpStatus;

public class CanNotDeletePost extends PostException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.UNAUTHORIZED;
    private static final String MESSAGE = "게시글 삭제 권한이 없습니다";

    public CanNotDeletePost() {
        super(MESSAGE, HTTP_STATUS);
    }
}
