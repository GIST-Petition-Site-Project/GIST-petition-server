package com.gistpetition.api.agreement;

import com.gistpetition.api.exception.petition.DuplicatedAgreementException;
import com.gistpetition.api.petition.domain.Agreement;
import com.gistpetition.api.petition.domain.Agreements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        assertThat(agreements.getAgreementList()).hasSize(0);

        Agreement agreement = new Agreement(DESCRIPTION, 1L);
        agreements.add(agreement);

        assertThat(agreements.getAgreementList()).hasSize(1);
    }

    @Test
    public void addTwiceBySameUser() {
        Agreement agreement = new Agreement(DESCRIPTION, 1L);
        agreements.add(agreement);

        assertThatThrownBy(() -> agreements.add(agreement)).isInstanceOf(DuplicatedAgreementException.class);
    }

    @Test
    public void isAgreedByWriter() {
        List<Long> userIdList = List.of(1L, 2L, 3L);
        for (Long userId : userIdList) {
            agreements.add(new Agreement(DESCRIPTION, userId));
        }

        Long writerId = userIdList.get(0);
        assertTrue(agreements.isAgreedBy(writerId));
    }

    @Test
    public void isAgreedByNotWriter() {
        int numOfWriter = 10;

        List<Long> userIdList = LongStream.range(0, numOfWriter).boxed().collect(Collectors.toList());
        for (Long userId : userIdList) {
            agreements.add(new Agreement(DESCRIPTION, userId));
        }

        Long notWriterId = userIdList.get(userIdList.size() - 1) + 1;
        assertFalse(agreements.isAgreedBy(notWriterId));
    }
}
