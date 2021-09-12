package com.example.gistcompetitioncnserver.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.gistcompetitioncnserver.post.Post;
import com.example.gistcompetitioncnserver.post.PostService;
import com.example.gistcompetitioncnserver.registration.RegistrationRequest;
import com.example.gistcompetitioncnserver.registration.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

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

    @PostMapping("/registration")
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
    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String email = decodedJWT.getSubject();
                User user = userService.getUser(email).get();

                String access_token = JWT.create()
                        .withSubject(user.getEmail())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("role", user.getUserRole().toString()) //check authorization.
                        .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            }catch (Exception exception) {
                response.setHeader("error", exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }


}
