package com.gistpetition.api.petition.application;

import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.Petition;

import java.util.function.Predicate;

public enum PetitionQueryCondition {
    WAITING_FOR_RELEASE((Petition p) -> !p.isReleased() && p.getAgreeCount() > 5);

    private Predicate<Petition> condition;

    PetitionQueryCondition(Predicate<Petition> condition) {
        this.condition = condition;
    }

    public Predicate<Petition> getCondition(Category category) {
        if (category == null) {
            return condition;
        }
        return condition.and(petition -> petition.getCategory() == category);
    }
}
