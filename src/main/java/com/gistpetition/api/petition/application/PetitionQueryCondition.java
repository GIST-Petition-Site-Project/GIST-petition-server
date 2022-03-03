package com.gistpetition.api.petition.application;

import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.Petition;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.time.Instant;

import static com.gistpetition.api.petition.application.PetitionQueryCondition.Flag.*;
import static com.gistpetition.api.petition.domain.QPetition.petition;

public enum PetitionQueryCondition {

    WAITING_FOR_RELEASE(
            petition.agreeCount.goe(Petition.REQUIRED_AGREEMENT_FOR_RELEASE).and(petition.released.isFalse()),
            NOT_EXPIRED
    ),
    WAITING_FOR_ANSWER(
            petition.agreeCount.goe(Petition.REQUIRED_AGREEMENT_FOR_ANSWER).and(petition.released.isTrue()).and(petition.answered.isFalse()),
            NO_MATTER
    ),
    ONGOING(
            petition.released.isTrue().and(petition.answered.isFalse()),
            NOT_EXPIRED
    ),
    EXPIRED(
            null,
            IS_EXPIRED
    );

    private final BooleanExpression condition;
    private final Flag needExpired;
    // private final Long flag;

    PetitionQueryCondition(BooleanExpression condition, Flag needExpired) {
        this.condition = condition;
        this.needExpired = needExpired;
    }

    public BooleanExpression getCondition(Instant at) {
        if (needExpired.equals(NOT_EXPIRED)) {
            return condition.and(petition.expiredAt.after(at));
        }
        if (needExpired.equals(Flag.IS_EXPIRED)) {
            return condition.and(petition.expiredAt.before(at));
        }
        return condition;
    }

    private BooleanExpression eqCategory(Category category) {
        return category == null ? null : petition.category.eq(category);
    }

    private BooleanExpression isExpiredPetition(Instant at) {
        return petition.expiredAt.before(at);
    }

    private BooleanExpression isReleasedPetition() {
        return petition.released.isTrue();
    }

    public enum Flag {
        NOT_EXPIRED,
        IS_EXPIRED,
        NO_MATTER
    }
}
