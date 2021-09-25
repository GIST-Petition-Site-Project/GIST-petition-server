package com.example.gistcompetitioncnserver.user;

import com.example.gistcompetitioncnserver.registration.token.EmailConfirmationToken;
import com.example.gistcompetitioncnserver.registration.token.EmailConfirmationTokenRepository;
import com.example.gistcompetitioncnserver.registration.token.EmailConfirmationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserRepository userRepository;

    // find user once users login
    private final static String USER_NOT_FOUND_MSG = "user with email %s not found";
    private final EmailConfirmationTokenService emailConfirmationTokenService;

    @Override // need to know how this work
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()) {
//            log.error("User not found in the database");
            throw new UsernameNotFoundException(USER_NOT_FOUND_MSG);
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.get().getUserRole().toString()));
        return new org.springframework.security.core.userdetails.User(user.get().getEmail(), user.get().getPassword(), authorities);

    }

    public Optional<User> getUser(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public String signUpUser(User user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword())); // encode password
        userRepository.save(user); // save user entity in db
        return createToken(user);
    }

    public String createToken(User user){
        String token = UUID.randomUUID().toString();

        // make confirmation token
        EmailConfirmationToken emailConfirmationToken = new EmailConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15), // set expirestime to 15 minutes
                user
        );

        //save confirmation token in database
        emailConfirmationTokenService.
                saveEmailConfirmToken(emailConfirmationToken);

        // return the token
        return token;
    }

    public int enableAppUser(String email) { return userRepository.enableAppUser(email); }


    public List<User> retrieveAllUsers(){
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id){
        return userRepository.findById(id);
    }

    public User save(User user){
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    public void deleteById(Long id){
        userRepository.deleteById(id);
    }

    public Optional<User> findUserByEmail(String email){
        return userRepository.findIdByEmail(email);
    }


}
