package com.gistpetition.api.petition.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class TempPetitionUrl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "petition_id", unique = true)
    private Long petitionId;
    @Column(name = "temp_url", unique = true)
    private String tempUrl;

    public TempPetitionUrl(Long petitionId, String tempUrl) {
        this(null, petitionId, tempUrl);
    }

    private TempPetitionUrl(Long id, Long petitionId, String tempUrl) {
        this.id = id;
        this.petitionId = petitionId;
        this.tempUrl = tempUrl;
    }
}
