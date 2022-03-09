package com.gistpetition.api.petition.application;

import com.gistpetition.api.petition.domain.Petition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.time.Instant;
import java.util.function.Function;

import static com.gistpetition.api.petition.application.PetitionQueryCondition.Expiration.*;
import static com.gistpetition.api.petition.application.PetitionQueryCondition.PetitionStatus.*;
import static com.gistpetition.api.petition.domain.QPetition.petition;

public enum PetitionQueryCondition {
    RELEASED(none, released),
    NOT_RELEASED(none, notReleased),
    ANSWERED(none, answered),
    NOT_ANSWERED(none, notAnswered),

    WAITING_FOR_RELEASE(notExpired, notReleased, agreeEnoughToRelease),
    WAITING_FOR_ANSWER(none, released, agreeEnoughToAnswer),
    ONGOING(notExpired, released, notAnswered),

    RELEASED_NOT_EXPIRED(notExpired, released),
    RELEASED_EXPIRED(expired, released);

    private final Expiration expiration;
    private final PetitionStatus[] conditions;

    PetitionQueryCondition(Expiration expiration, PetitionStatus... conditions) {
        this.expiration = expiration;
        this.conditions = conditions;
    }

    public Predicate at(Instant at) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(this.expiration.at(at));
        for (PetitionStatus qc : conditions) {
            booleanBuilder.and(qc.condition);
        }
        return booleanBuilder;
    }

    enum Expiration {
        notExpired(i -> petition.expiredAt.after(i)),
        expired(i -> petition.expiredAt.before(i)),
        none(i -> null);

        final Function<Instant, BooleanExpression> function;

        Expiration(Function<Instant, BooleanExpression> function) {
            this.function = function;
        }

        public BooleanExpression at(Instant time) {
            return function.apply(time);
        }
    }

    enum PetitionStatus {
        released(petition.released.isTrue()),
        notReleased(petition.released.isFalse()),
        answered(petition.answered.isTrue()),
        notAnswered(petition.answered.isFalse()),
        agreeEnoughToRelease(petition.agreeCount.goe(Petition.REQUIRED_AGREEMENT_FOR_RELEASE)),
        agreeEnoughToAnswer(petition.agreeCount.goe(Petition.REQUIRED_AGREEMENT_FOR_ANSWER));

        private final BooleanExpression condition;

        PetitionStatus(BooleanExpression condition) {
            this.condition = condition;
        }
    }
}
