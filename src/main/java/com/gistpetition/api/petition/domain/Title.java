package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.InvalidTitleLengthException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Title {
    public static final int TITLE_MAX_LENGTH = 100;

    @Column(name = "title", nullable = false)
    private String title;

    public Title(String title) {
        validateTitle(title);
        this.title = title;
    }

    public void update(String title) {
        validateTitle(title);
        this.title = title;
    }

    private void validateTitle(String title) {
        if (Objects.isNull(title) || title.isBlank() || title.length() > TITLE_MAX_LENGTH) {
            throw new InvalidTitleLengthException();
        }
    }
}
