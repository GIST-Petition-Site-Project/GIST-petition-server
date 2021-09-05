package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.registration.token.EmailConfirmationToken;
import com.example.gistcompetitioncnserver.registration.token.EmailConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserRepository userRepository;

    // find user once users login
    private final static String USER_NOT_FOUND_MSG =
            "user with email %s not found";
    private final EmailConfirmationTokenService emailConfirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String signUpUser(User user){

        // check user exists to prevent duplication
        boolean userExists = userRepository.findByEmail(user.getEmail())
                .isPresent();

        if (userExists){
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        // encode password
        String encodedPassword = bCryptPasswordEncoder
                .encode(user.getPassword());

        user.setUserPassword(encodedPassword);

        userRepository.save(user); // save db

        String token = UUID.randomUUID().toString();

        // send confirmation token
        EmailConfirmationToken emailConfirmationToken = new EmailConfirmationToken(
                token,
                LocalDateTime.now(),
                // set expirestime to 15 minutes
                LocalDateTime.now().plusMinutes(15),
                user
        );

        //save email token
        emailConfirmationTokenService.
                saveEmailConfirmToken(emailConfirmationToken);

        // send email token to RegistrationService
        return token;

    }

    public int enableAppUser(String email) {
        return userRepository.enableAppUser(email);
    }



    public List<User> retrieveAllUsers(){
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id){
        return userRepository.findById(id);
    }

    @Transactional
    public User save(User user){
//        System.out.println("user = " + user);

        String encodedPassword = bCryptPasswordEncoder.encode(user.getUserPassword());
        user.setUserPassword(encodedPassword);

        return userRepository.save(user);

    }

    @Transactional
    public void deleteById(Long id){
        userRepository.deleteById(id);
    }


}
