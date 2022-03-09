package com.gistpetition.api.unit.utils;

import com.gistpetition.api.utils.urlGenerator.RandomUrlGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RandomUrlGeneratorTest {

    private RandomUrlGenerator tempUrlGenerator;

    @BeforeEach
    void setUp() {
        tempUrlGenerator = new RandomUrlGenerator();
    }

    @Test
    void generate() {
        assertThat(tempUrlGenerator.generate(6)).hasSize(6);
    }
}