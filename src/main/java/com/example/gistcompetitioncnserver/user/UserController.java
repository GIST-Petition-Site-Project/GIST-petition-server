package com.example.gistcompetitioncnserver.user;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.gistcompetitioncnserver.exception.CustomException;
import com.example.gistcompetitioncnserver.exception.ErrorCase;
import com.example.gistcompetitioncnserver.exception.ErrorMessage;
import com.example.gistcompetitioncnserver.registration.RegistrationRequest;
import com.example.gistcompetitioncnserver.registration.RegistrationService;
import com.example.gistcompetitioncnserver.registration.token.EmailConfirmationToken;
import com.example.gistcompetitioncnserver.registration.token.EmailConfirmationTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final RegistrationService registrationService;
    private final EmailConfirmationTokenService emailConfirmationTokenService;

    @PostMapping("/users")
    public ResponseEntity<Object> register(@RequestBody RegistrationRequest request, HttpServletRequest urlRequest) {
        String email = request.getEmail();
        Optional<User> user = userService.findUserByEmail(email);
        if (user.isPresent()) {
            throw new CustomException(ErrorCase.USER_ALREADY_EXIST);
        }

        if (!email.contains("@gm.gist.ac.kr") && !email.contains("@gist.ac.kr")) {
            throw new CustomException(ErrorCase.INVALID_EMAIL);
        }

        return ResponseEntity.created(URI.create("/user/" + registrationService.register(request, urlRequest))).build();
    }

    @GetMapping("/users")
    public List<User> retrieveAllUsers() {
        return userService.retrieveAllUsers();
    }

    @GetMapping("/users/{userId}")
    public EntityModel<User> retrieveUser(@PathVariable long userId) {

        Optional<User> user = userService.findUserById(userId);

        if (!user.isPresent()) {
            throw new UsernameNotFoundException(String.format("ID[%s] not found", userId));
        }

        EntityModel<User> resource = new EntityModel<>(user.get());
        WebMvcLinkBuilder linkTo = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());
        resource.add(linkTo.withRel("all-users"));
        return resource;

    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteById(userId);
    }

    @GetMapping("/resend-email")
    public ResponseEntity<Object> resendEmailVerification(@AuthenticationPrincipal String email,
                                                          HttpServletRequest urlRequest) {

        User user = userService.findUserByEmail(email).get();
        if (user.isEnabled()) {
            throw new CustomException(ErrorCase.USER_ALREADY_EXIST);
        }

        return ResponseEntity.ok().body(registrationService.resendEmail(user, urlRequest));
    }


    @GetMapping("/confirm")
    public ResponseEntity<Object> confirm(@RequestParam("token") String token, HttpServletResponse response)
            throws IOException {
        Optional<EmailConfirmationToken> emailConfirmationToken = emailConfirmationTokenService.getToken(token);
        if (emailConfirmationToken.isEmpty()) {
            throw new CustomException(ErrorCase.NO_SUCH_TOKEN_ERROR);
        }

        if (emailConfirmationToken.get().getConfirmedAt() != null) {
            throw new CustomException(ErrorCase.USER_ALREADY_EXIST);
        }

        LocalDateTime expiredAt = emailConfirmationToken.get().getExpiredAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCase.EXPIRED_TOKEN_ERROR);
        }

        response.sendRedirect(registrationService.confirmToken(token,
                emailConfirmationToken.get().getUser().getEmail())); // redirect the main page
        return ResponseEntity.badRequest()
                .body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.BAD_REQUEST_ERROR));
    }


    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
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
            } catch (Exception exception) {
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
