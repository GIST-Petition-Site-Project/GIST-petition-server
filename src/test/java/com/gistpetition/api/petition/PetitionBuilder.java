package com.gistpetition.api.petition;

import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.Petition;

import java.time.LocalDateTime;

public final class PetitionBuilder {
    private Boolean answered = false;
    private String title = "제목";
    private String description = "내용";
    private Category category = Category.DORMITORY;
    private LocalDateTime expiredAt = LocalDateTime.now().plusDays(Petition.POSTING_PERIOD);
    private Long userId;
    private String tempUrl;

    private PetitionBuilder() {
    }

    public static PetitionBuilder aPetition() {
        return new PetitionBuilder();
    }

    public PetitionBuilder withAnswered(Boolean answered) {
        this.answered = answered;
        return this;
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

    public PetitionBuilder withExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
        return this;
    }

    public PetitionBuilder withUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public PetitionBuilder withTempUrl(String tempUrl) {
        this.tempUrl = tempUrl;
        return this;
    }

    public Petition build() {
        Petition petition = new Petition(title, description, category, expiredAt, userId, tempUrl);
        petition.setAnswered(answered);
        return petition;
    }
}
