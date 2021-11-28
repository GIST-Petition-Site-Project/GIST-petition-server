package com.example.gistcompetitioncnserver.user;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    void findUserById() {
        assertThatThrownBy(() -> userService.findUserByEmail2("hello@hello.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
