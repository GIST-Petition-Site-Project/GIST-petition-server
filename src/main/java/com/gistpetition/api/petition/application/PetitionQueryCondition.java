package com.gistpetition.api.petition.application;

import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.Petition;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.time.Instant;
import java.util.function.Function;

import static com.gistpetition.api.petition.domain.QPetition.petition;

public enum PetitionQueryCondition {
    RELEASED(petition.released.isTrue(), Expiration.NONE),
    NOT_RELEASED(petition.released.isFalse(), Expiration.NONE),
    ANSWERED(petition.answered.isTrue(), Expiration.NONE),
    NOT_ANSWERED(petition.answered.isFalse(), Expiration.NONE),

    WAITING_FOR_RELEASE(
            NOT_RELEASED.condition.and(petition.agreeCount.goe(Petition.REQUIRED_AGREEMENT_FOR_RELEASE)),
            Expiration.NOT_EXPIRED),
    WAITING_FOR_ANSWER(
            RELEASED.condition.and(NOT_ANSWERED.condition).and(petition.agreeCount.goe(Petition.REQUIRED_AGREEMENT_FOR_ANSWER)),
            Expiration.NONE),
    ONGOING(
            RELEASED.condition.and(NOT_ANSWERED.condition),
            Expiration.NOT_EXPIRED),
    RELEASED_NOT_EXPIRED(
            RELEASED.condition,
            Expiration.NOT_EXPIRED),
    RELEASED_EXPIRED(
            RELEASED.condition,
            Expiration.IS_EXPIRED);

    private final BooleanExpression condition;
    private final Expiration expiration;

    PetitionQueryCondition(BooleanExpression condition, Expiration expiration) {
        this.condition = condition;
        this.expiration = expiration;
    }

    public BooleanExpression of(Category category, Instant at) {
        BooleanExpression be = condition.and(expiration.at(at));
        return category == null ? be : be.and(petition.category.eq(category));
    }

    public enum Expiration {
        NOT_EXPIRED(i -> petition.expiredAt.after(i)),
        IS_EXPIRED(i -> petition.expiredAt.before(i)),
        NONE(i -> null);

        final Function<Instant, BooleanExpression> function;

        Expiration(Function<Instant, BooleanExpression> function) {
            this.function = function;
        }

        public BooleanExpression at(Instant time) {
            return function.apply(time);
        }
    }
}
