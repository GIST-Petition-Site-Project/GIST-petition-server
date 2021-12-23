package com.example.gistcompetitioncnserver.answer;

import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.exception.ErrorCase;
import com.example.gistcompetitioncnserver.post.PostRepository;
import com.example.gistcompetitioncnserver.post.PostRequestDto;
import com.example.gistcompetitioncnserver.post.PostService;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/v1") // not used in rest api like v1 url
public class AnswerController {

    private final AnswerService answerService;
    private final PostService postService;
    private final UserService userService;
    private final PostRepository postRepository;

    private boolean isRequestBodyValid(AnswerRequestDto answerRequestDto) {
        return answerRequestDto.getContent() != null ;
    }
    @PostMapping("/posts/{postId}/answer")
    public ResponseEntity<Object> createAnswer(@PathVariable Long postId, @RequestBody AnswerRequestDto answerRequestDto,
                                             @AuthenticationPrincipal String email) {
        User user = userService.findUserByEmail2(email);

        if (!isRequestBodyValid(answerRequestDto)) {
            throw new CustomException(ErrorCase.INVAILD_FILED_ERROR);
        }

        return ResponseEntity.created(URI.create("/posts/" + postId + answerService.createAnswer(postId, answerRequestDto, user.getId())))
                .build();
    }

    @GetMapping("/posts/{postId}/answer/{answerId}")
    public Optional<Answer> retrieveAnswer(@PathVariable Long postId,@PathVariable Long answerId){
        return answerService.retrieveAnswer(answerId);
    }


//    @DeleteMapping("/posts/{postId}/answer/{answerId}")
//    public void deleteAnswer(@PathVariable Long postId,@PathVariable Long answerId)){
//        answerService.deleteAnswer(answerId);
//    }




}
