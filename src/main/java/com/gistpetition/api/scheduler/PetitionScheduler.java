package com.gistpetition.api.scheduler;

import com.gistpetition.api.petition.domain.repository.PetitionQueryDslRepository;
import com.gistpetition.api.petition.dto.PetitionPreviewResponse;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import com.gistpetition.api.utils.emailsender.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static com.gistpetition.api.petition.application.PetitionQueryCondition.WAITING_FOR_ANSWER;
import static com.gistpetition.api.petition.application.PetitionQueryCondition.WAITING_FOR_RELEASE;

@Slf4j
@Component
@Profile("dev || prod")
@RequiredArgsConstructor
public class PetitionScheduler {
    @Value("${staff.url:https://staff.gist-petition.com}")
    private String staffUrl;
    private final UserRepository userRepository;
    private final PetitionQueryDslRepository petitionQueryDslRepository;
    private final SpringTemplateEngine springTemplateEngine;
    private final EmailSender emailSender;

    @Scheduled(cron = "0 0 9 * * MON-FRI", zone = "Asia/Seoul")
    public void schedule() {
        List<User> managers = userRepository.findAllByUserRole(UserRole.MANAGER);
        List<PetitionPreviewResponse> waitingForRelease = petitionQueryDslRepository.findAll(null, WAITING_FOR_RELEASE.at(Instant.now()));
        List<PetitionPreviewResponse> waitingForAnswer = petitionQueryDslRepository.findAll(null, WAITING_FOR_ANSWER.at(Instant.now()));

        List<String> usernames = managers.stream().map(User::getUsername).collect(Collectors.toList());
        String subject = String.format("[지스트 청원] 오늘의 승인 대기 중인 청원: %d, 답변 대기 중인 청원: %d", waitingForRelease.size(), waitingForAnswer.size());
        String body = generateBody(waitingForRelease, waitingForAnswer);

        log.info("Scheduled Email send to manager => " + usernames);
        log.info("Scheduled Email with subject => " + subject);
        emailSender.send(usernames, subject, body);
    }

    private String generateBody(List<PetitionPreviewResponse> waitingForRelease, List<PetitionPreviewResponse> waitingForAnswer) {
        Context context = new Context();
        context.setVariable("waitingForRelease", waitingForRelease);
        context.setVariable("waitingForAnswer", waitingForAnswer);
        context.setVariable("baseLinkToRelease", staffUrl + "/approve/");
        context.setVariable("baseLinkToAnswer", staffUrl + "/answer/");
        return springTemplateEngine.process("petition_scheduler.html", context);
    }
}
