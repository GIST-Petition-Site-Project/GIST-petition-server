package com.gistpetition.api.agreement;

import com.gistpetition.api.exception.petition.DuplicatedAgreementException;
import com.gistpetition.api.petition.domain.Agreement;
import com.gistpetition.api.petition.domain.Agreements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgreementsTest extends Agreements {
    private final String DESCRIPTION = "description";
    private AgreementsSupport agreements;

    @BeforeEach
    void setup() {
        agreements = new AgreementsSupport();
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
        Long writerId = 1L;
        agreements.add(new Agreement(DESCRIPTION, writerId));
        assertTrue(agreements.isAgreedBy(writerId));
    }

    @Test
    public void isAgreedBy_fail() {
        Long fakeWriterId = Long.MAX_VALUE;
        assertTrue(agreements.isAgreedBy(fakeWriterId));
    }
}
