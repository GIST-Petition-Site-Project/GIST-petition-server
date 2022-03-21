package com.gistpetition.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.TimeZone;

@Slf4j
@EnableAspectJAutoProxy
@EnableScheduling
@EnableAsync
@SpringBootApplication
public class GistPetitionApplication {

    @PostConstruct
    public void initTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        log.info("current Timezone : {}, current DateTime : {}", TimeZone.getDefault().getDisplayName(), ZonedDateTime.now());
    }

    public static void main(String[] args) {
        SpringApplication.run(GistPetitionApplication.class, args);
    }
}
