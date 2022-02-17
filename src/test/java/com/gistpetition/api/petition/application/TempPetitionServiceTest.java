package com.gistpetition.api.petition.application;

import com.gistpetition.api.ServiceTest;
import com.gistpetition.api.exception.petition.DuplicatedTempUrlException;
import com.gistpetition.api.exception.petition.NoSuchTempUrlException;
import com.gistpetition.api.petition.domain.TempPetitionUrl;
import com.gistpetition.api.petition.domain.TempPetitionUrlRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TempPetitionServiceTest extends ServiceTest {
    @Autowired
    private TempPetitionService tempPetitionService;
    @Autowired
    private TempPetitionUrlRepository tempPetitionUrlRepository;

    @Test
    void createTempPetition() {
        Long petitionId = 1L;
        String tempUrl = tempPetitionService.createTempUrl(petitionId);

        Optional<TempPetitionUrl> tempPetitionUrl = tempPetitionUrlRepository.findByTempUrl(tempUrl);
        assertThat(tempPetitionUrl).isPresent();
    }

    @Test
    void createTempPetitionAlreadyExists() {
        Long petitionId = 1L;
        tempPetitionService.createTempUrl(petitionId);
        assertThatThrownBy(
                () -> tempPetitionService.createTempUrl(petitionId)
        ).isInstanceOf(DuplicatedTempUrlException.class);
    }


    @Test
    void createTempPetitionWithSameTempUrl() {
        String notExistingTempUrl = "NOT_TEMP";

        assertThatThrownBy(
                () -> tempPetitionService.findPetitionIdByTempUrl(notExistingTempUrl)
        ).isInstanceOf(NoSuchTempUrlException.class);
    }

    @AfterEach
    void tearDown() {
        tempPetitionUrlRepository.deleteAllInBatch();
    }
}