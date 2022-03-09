package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.DuplicatedAgreementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgreementsTest {
    private static final String DESCRIPTION = "description";
    private Agreements agreements;

    @BeforeEach
    void setup() {
        agreements = new Agreements();
    }

    @Test
    public void add() {
        Agreement agreement = new Agreement(DESCRIPTION, 1L);
        agreements.add(agreement);

        assertThat(agreements.getAgreements()).hasSize(1);
        assertThat(agreements.getAgreements().get(0).getDescription()).isEqualTo(DESCRIPTION);
    }

    @Test
    public void addTwiceBySameUser() {
        Agreement agreement = new Agreement(DESCRIPTION, 1L);
        agreements.add(agreement);

        assertThatThrownBy(() -> agreements.add(agreement)).isInstanceOf(DuplicatedAgreementException.class);
    }

    @Test
    public void isAgreedBy() {
        Long userId = 1L;

        agreements.add(new Agreement(DESCRIPTION, userId));

        assertTrue(agreements.isAgreedBy(userId));
    }

    @Test
    public void isAgreedBy_fail() {
        Long notAgreedUserId = Long.MAX_VALUE;

        assertFalse(agreements.isAgreedBy(notAgreedUserId));
    }
}
