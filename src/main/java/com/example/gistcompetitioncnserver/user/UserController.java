package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostService;
import com.example.gistcompetitioncnserver.registration.RegistrationRequest;
import com.example.gistcompetitioncnserver.registration.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gistps/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private RegistrationService registrationService;


    @GetMapping("")
    public List<User> retrieveAllUsers(){
        return userService.retrieveAllUsers();
    }

    @GetMapping("/{id}")
    public EntityModel<User> retrieveUser(@PathVariable long id){

        Optional<User> user = userService.findUserById(id);

        if(!user.isPresent()){
            throw new UsernameNotFoundException(String.format("ID[%s] not found", id));
        }

        // use HATEOS
        EntityModel<User> resource = new EntityModel<>(user.get());
        WebMvcLinkBuilder linkTo = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());
        resource.add(linkTo.withRel("all-users"));

        return resource;

    }

    @GetMapping("/{userId}/post")
    public List<Post> retrievePostsByUserId(@PathVariable long userId){
        return postService.retrievePostsByUserId(userId);
    }

    @PostMapping("")
    public ResponseEntity<User> createUser(@RequestBody User user){

        User savedUser = userService.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.deleteById(id);
    }

    //MARK: - rr

    @PostMapping("/registeration")
    public String register(@RequestBody RegistrationRequest request){
        return registrationService.register(request);
    }

    //로그인 시도하려면 이렇게 해도?
    @GetMapping( "/confirm")
    public String confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
        //Registrationservice로
    }

//    @PostMapping(path = "/login")
//    public String login(@RequestBody LoginRequest request){
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 회원입니다."));
//        if (!bCryptPasswordEncoder.encode(request.getPassword())
//                .matches(user.getPassword())){
//            throw new IllegalStateException("잘못된 비밀번호입니다.");
//        }
//        return jwtTokenProvider.createToken(user.getUsername(), user.getUserRole());
//    }


}
