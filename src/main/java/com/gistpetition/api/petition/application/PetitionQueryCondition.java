package com.gistpetition.api.petition.application;

import com.gistpetition.api.petition.domain.Petition;
import com.gistpetition.api.petition.domain.Status;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.time.Instant;
import java.util.function.Function;

import static com.gistpetition.api.petition.application.PetitionQueryCondition.ExpirationCondition.*;
import static com.gistpetition.api.petition.application.PetitionQueryCondition.PetitionStatus.*;
import static com.gistpetition.api.petition.domain.QAgreeCount.agreeCount;
import static com.gistpetition.api.petition.domain.QPetition.petition;

public enum PetitionQueryCondition {
    NOT_TEMPORARY(none, non_temporary),

    WAITING_FOR_RELEASE(notExpired, non_temporary, agreeEnoughToRelease),
    ONGOING(notExpired, released),
    EXPIRED(expired, released),
    REJECTED(none, rejected),
    WAITING_FOR_ANSWER(none, released, agreeEnoughToAnswer),
    ANSWERED(none, answered);

    private final ExpirationCondition expirationCondition;
    private final PetitionStatus[] conditions;

    PetitionQueryCondition(ExpirationCondition expirationCondition, PetitionStatus... conditions) {
        this.expirationCondition = expirationCondition;
        this.conditions = conditions;
    }

    public Predicate at(Instant at) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (PetitionStatus ps : conditions) {
            booleanBuilder.and(ps.condition);
        }
        booleanBuilder.and(this.expirationCondition.at(at));
        return booleanBuilder;
    }

    enum ExpirationCondition {
        notExpired(petition.expiredAt::after),
        expired(petition.expiredAt::before),
        none(i -> null);

        final Function<Instant, BooleanExpression> function;

        ExpirationCondition(Function<Instant, BooleanExpression> function) {
            this.function = function;
        }

        public BooleanExpression at(Instant time) {
            return function.apply(time);
        }
    }

    enum PetitionStatus {
        temporary(petition.status.eq(Status.TEMPORARY)),
        non_temporary(petition.status.ne(Status.TEMPORARY)),
        released(petition.status.eq(Status.RELEASED)),
        rejected(petition.status.eq(Status.REJECTED)),
        answered(petition.status.eq(Status.ANSWERED)),
        agreeEnoughToRelease(agreeCount.count.goe(Petition.REQUIRED_AGREEMENT_FOR_RELEASE)),
        agreeEnoughToAnswer(agreeCount.count.goe(Petition.REQUIRED_AGREEMENT_FOR_ANSWER));

        private final BooleanExpression condition;

        PetitionStatus(BooleanExpression condition) {
            this.condition = condition;
        }
    }
}
