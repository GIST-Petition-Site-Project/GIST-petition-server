package com.gistpetition.api;

import com.gistpetition.api.utils.emailsender.EmailSender;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public abstract class ServiceTest {
    @MockBean
    EmailSender emailSender;
}
