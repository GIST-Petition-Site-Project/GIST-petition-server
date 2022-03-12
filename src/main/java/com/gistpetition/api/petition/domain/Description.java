package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.InvalidDescriptionLengthException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Description {
    public static final int DESCRIPTION_MAX_LENGTH = 10000;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    public Description(String description) {
        validateDescription(description);
        this.description = description;
    }

    public void update(String description) {
        validateDescription(description);
        this.description = description;
    }

    private void validateDescription(String description) {
        if (Objects.isNull(description) || description.isBlank() || description.length() > DESCRIPTION_MAX_LENGTH) {
            throw new InvalidDescriptionLengthException();
        }
    }
}
