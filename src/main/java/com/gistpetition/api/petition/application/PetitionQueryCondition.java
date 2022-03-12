package com.gistpetition.api.petition.application;

import com.gistpetition.api.petition.domain.Petition;
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
    RELEASED(none, released),
    NOT_RELEASED(none, notReleased),
    ANSWERED(none, answered),
    NOT_ANSWERED(none, notAnswered),

    WAITING_FOR_RELEASE(notExpired, notReleased, agreeEnoughToRelease),
    ONGOING(notExpired, released, notAnswered),
    WAITING_FOR_ANSWER(none, released, notAnswered, agreeEnoughToAnswer),

    RELEASED_NOT_EXPIRED(notExpired, released),
    RELEASED_EXPIRED(expired, released);

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
        notExpired(i -> petition.expiredAt.after(i)),
        expired(i -> petition.expiredAt.before(i)),
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
        released(petition.released.isTrue()),
        notReleased(petition.released.isFalse()),
        answered(petition.answer.isNotNull()),
        notAnswered(petition.answer.isNull()),
        agreeEnoughToRelease(agreeCount.count.goe(Petition.REQUIRED_AGREEMENT_FOR_RELEASE)),
        agreeEnoughToAnswer(agreeCount.count.goe(Petition.REQUIRED_AGREEMENT_FOR_ANSWER));

        private final BooleanExpression condition;

        PetitionStatus(BooleanExpression condition) {
            this.condition = condition;
        }
    }
}
