package com.gistpetition.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.TimeZone;

@Slf4j
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
