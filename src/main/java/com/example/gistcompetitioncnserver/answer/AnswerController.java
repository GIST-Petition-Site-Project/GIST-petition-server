package com.example.gistcompetitioncnserver.answer;

import com.example.gistcompetitioncnserver.post.PostRepository;
import com.example.gistcompetitioncnserver.post.PostService;
import com.example.gistcompetitioncnserver.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/gistps/api/v1/answer") // not used in rest api like v1 url
public class AnswerController {

    private final AnswerService answerService;
    private final PostService postService;
    private final UserService userService;
    private final PostRepository postRepository;

//    @PostMapping("/{id}")
//    public Long createAnswer(@PathVariable Long id, @RequestBody Answer answer){
//
//        Optional<User> isEmployee = userDaoService.findUserById(answer.getUserId());
//        Optional<Post> tobeAnsweredPost = null;
//        Answer savedAnswer = null;
//
//        //현재 글을 작성하는 유저가 직원 타입이  맞는지 확인
//        //답변 생성 및 답변 된 게시글의 상태를 answer로 변경
//        //답변의 id를 return
//        if(isEmployee.get().getUsertype().equals("employee")) {
//            savedAnswer = answerService.createAnswer(answer, postService.retrievePost(id).get());
//            postService.updateAnsweredPost(id);
//            return savedAnswer.getId();
//        }
//
//        return -1L;
//    }

    @GetMapping("")
    public List<Answer> retrieveAllPost(){
        return answerService.retrieveAllAnswers();
    }

    @GetMapping("/{id}")
    public Optional<Answer> retrieveAnswer(@PathVariable Long id){
        return answerService.retrieveAnswer(id);
    }

    @GetMapping("/count")
    public Long getPageNumber(){
        return answerService.getPageNumber();
    }

    @GetMapping("/category")
    public List<Answer> getPostsByCategory(@RequestParam("categoryName") String categoryName){
        return answerService.getAnswersByCategory(categoryName);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id){
        answerService.deleteAnswer(id);
    }


//    @PutMapping("/{id}")
//    public void amendPost(@PathVariable Long id, @RequestBody Post post){
//        postRepository.
//    }

}
