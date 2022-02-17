package com.gistpetition.api.utils.urlGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.gistpetition.api.petition.application.TempPetitionService.TEMP_URL_LENGTH;
import static org.assertj.core.api.Assertions.assertThat;

class RandomUrlGeneratorTest {

    private RandomUrlGenerator tempUrlGenerator;

    @BeforeEach
    void setUp() {
        tempUrlGenerator = new RandomUrlGenerator();
    }

    @Test
    void generate() {
        assertThat(tempUrlGenerator.generate(TEMP_URL_LENGTH)).hasSize(TEMP_URL_LENGTH);
    }
}