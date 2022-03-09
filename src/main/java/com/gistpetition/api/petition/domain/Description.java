package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.InvalidDescriptionException;
import com.gistpetition.api.exception.petition.InvalidTitleException;
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
    @Column
    @Lob
    private String description;
    public static final int DESCRIPTION_MAX_LENGTH = 10000;

    public Description(String description){
        if (Objects.isNull(description) || description.isBlank() || description.length()>DESCRIPTION_MAX_LENGTH) {
            throw new InvalidDescriptionException();
        }
        this.description = description;
    }
}
