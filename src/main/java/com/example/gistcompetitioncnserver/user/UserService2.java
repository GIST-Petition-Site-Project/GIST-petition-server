package com.example.gistcompetitioncnserver.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserService2 {
    private final User2Repository user2Repository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService2(User2Repository user2Repository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.user2Repository = user2Repository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public Long signUp(SignUpRequest request) {
        User2 user = new User2(
                request.getUsername(),
                bCryptPasswordEncoder.encode(request.getPassword()),
                UserRole.USER);
        return user2Repository.save(user).getId();
    }
}

