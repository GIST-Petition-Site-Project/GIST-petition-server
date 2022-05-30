package com.gistpetition.api.petition;

import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.Petition;

public final class PetitionBuilder {
    private String title = "제목";
    private String description = "내용";
    private Category category = Category.DORMITORY;
    private Long userId;

    private PetitionBuilder() {
    }

    public static PetitionBuilder aPetition() {
        return new PetitionBuilder();
    }

    public PetitionBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public PetitionBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public PetitionBuilder withCategory(Category category) {
        this.category = category;
        return this;
    }

    public PetitionBuilder withUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Petition build() {
        return new Petition(title, description, category, userId);
    }
}
