package com.example.gistcompetitioncnserver.answer;

import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostRepository;
import com.example.gistcompetitioncnserver.post.PostService;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserDaoService;
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
    private final UserDaoService userDaoService;
    private final PostRepository postRepository;

    @PostMapping("/{id}")
    public Long createAnswer(@PathVariable Long id, @RequestBody Answer answer){

        Optional<User> isEmployee = userDaoService.findUserById(answer.getUserId());
        Optional<Post> tobeAnsweredPost = null;
        Answer savedAnswer = null;

        //현재 글을 작성하는 유저가 직원 타입이  맞는지 확인 후 답변할 글을 불러옴.
        if(isEmployee.get().getUsertype().equals("employee")) {
            tobeAnsweredPost = postService.retrievePost(id);
        }
        else return -1L;

        //답변할 글이 현재 존재하는지 확인 후 답변 생성.
        if(tobeAnsweredPost.isPresent()) {
            savedAnswer = answerService.createAnswer(answer, tobeAnsweredPost.get());
        }
        else return -2L;

        //해당게시글에 대한 답변이 생성되었는지 확인 후 게시글의 답변 상태를 true로 변경.
        if(answerService.retrieveAnswer(id).isPresent()) {
            postService.updateAnsweredPost(id);
        }
        else return -3L;

        return savedAnswer.getId();
    }

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
