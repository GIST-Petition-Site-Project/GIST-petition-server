package com.example.gistcompetitioncnserver.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.gistcompetitioncnserver.common.ErrorCase;
import com.example.gistcompetitioncnserver.common.ErrorMessage;
import com.example.gistcompetitioncnserver.registration.RegistrationRequest;
import com.example.gistcompetitioncnserver.registration.RegistrationService;
import com.example.gistcompetitioncnserver.registration.token.EmailConfirmationToken;
import com.example.gistcompetitioncnserver.registration.token.EmailConfirmationTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping("/gistps/api/v1/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final RegistrationService registrationService;
    private final EmailConfirmationTokenService emailConfirmationTokenService;


    @GetMapping("")
    public List<User> retrieveAllUsers(){
        return userService.retrieveAllUsers();
    }

    @GetMapping("/{id}") // user 조회 방식 논의 필요
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


//    @PostMapping("")
//    public ResponseEntity<User> createUser(@RequestBody User user){
//        User savedUser = userService.save(user);
//        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(savedUser)
//                .toUri();
//        return ResponseEntity.created(location).build();
//    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.deleteById(id);
    }

    //MARK: - try registartion and send email
    @PostMapping("/registration")
    public ResponseEntity<Object> register(@RequestBody RegistrationRequest request, HttpServletRequest urlRequest){
        String email = request.getEmail();
        Optional<User> user = userService.findUserByEmail(email);
        if(user.isPresent()){
            return ResponseEntity.badRequest().body(
                    new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.USER_ALREADY_EXIST)
            );
        }

        if (!email.contains("@gm.gist.ac.kr") && !email.contains("@gist.ac.kr")){
            return ResponseEntity.badRequest().body(
                    new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.INVALID_EMAIL)
            );
        }

        return ResponseEntity.created(URI.create("/user/" + registrationService.register(request, urlRequest))).build();
    }

    @GetMapping("/resend-email")
    public ResponseEntity<Object> resendEmailVerification(@AuthenticationPrincipal String email, HttpServletRequest urlRequest) {

        User user = userService.findUserByEmail(email).get();
        if (user.isEnabled()) { // enabled 된 계정이면 안돼
            return ResponseEntity.badRequest().body(
                    new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.USER_ALREADY_EXIST)
            );
        }

        return ResponseEntity.ok().body(registrationService.resendEmail(user, urlRequest));
    }



    @GetMapping( "/confirm")
    public ResponseEntity<Object> confirm(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        Optional<EmailConfirmationToken> emailConfirmationToken = emailConfirmationTokenService.getToken(token);
        if (emailConfirmationToken.isEmpty()){
            return ResponseEntity.badRequest().body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_TOKEN_ERROR));
        }

        if(emailConfirmationToken.get().getConfirmedAt() != null){
            return ResponseEntity.badRequest().body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.USER_ALREADY_EXIST));
        }

        LocalDateTime expiredAt = emailConfirmationToken.get().getExpiredAt();

        if (expiredAt.isBefore(LocalDateTime.now())){
            return ResponseEntity.badRequest().body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.EXPIRED_TOKEN_ERROR));
        }

        response.sendRedirect(registrationService.confirmToken(token, emailConfirmationToken.get().getUser().getEmail())); // redirect the main page
        return ResponseEntity.badRequest().body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.BAD_REQUEST_ERROR));
    }


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
