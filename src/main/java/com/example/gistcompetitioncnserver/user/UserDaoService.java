package com.example.gistcompetitioncnserver.user;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserDaoService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User save(User user){
        System.out.println("user = " + user);

        String encodedPassword = bCryptPasswordEncoder.encode(user.getUserPassword());
        user.setUserPassword(encodedPassword);

        return userRepository.save(user);

    }

}
