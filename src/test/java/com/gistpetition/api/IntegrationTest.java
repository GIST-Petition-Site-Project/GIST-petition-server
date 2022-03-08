package com.gistpetition.api;

import com.gistpetition.api.utils.emailsender.EmailSender;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;

@Sql(value = "/clear.sql")
@SpringBootTest
public abstract class IntegrationTest {
    @MockBean
    EmailSender emailSender;
}
