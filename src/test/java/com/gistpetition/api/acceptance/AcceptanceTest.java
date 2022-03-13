package com.gistpetition.api.acceptance;

import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import com.gistpetition.api.utils.emailsender.EmailSender;
import com.gistpetition.api.utils.password.Encoder;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static com.gistpetition.api.acceptance.common.TUser.T_ADMIN;
import static com.gistpetition.api.acceptance.common.TUser.T_MANAGER;

@Sql(value = "/clear.sql")
@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {
    @LocalServerPort
    int port;
    @Autowired
    Encoder encoder;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        userRepository.save(new User(T_ADMIN.getUsername(), encoder.hashPassword(T_ADMIN.getPassword()), UserRole.ADMIN));
        userRepository.save(new User(T_MANAGER.getUsername(), encoder.hashPassword(T_MANAGER.getPassword()), UserRole.MANAGER));
    }

    @MockBean
    EmailSender emailSender;
}
