package com.gistpetition.api.petition.application;

import com.gistpetition.api.exception.petition.DuplicatedTempUrlException;
import com.gistpetition.api.exception.petition.NoSuchTempUrlException;
import com.gistpetition.api.petition.domain.TempPetitionUrl;
import com.gistpetition.api.petition.domain.TempPetitionUrlRepository;
import com.gistpetition.api.utils.urlGenerator.UrlGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TempPetitionService {
    public static final int TEMP_URL_LENGTH = 6;

    private final TempPetitionUrlRepository tempPetitionUrlRepository;
    private final UrlGenerator urlGenerator;

    @Transactional
    public String createTempUrl(Long petitionId) {
        if (tempPetitionUrlRepository.existsByPetitionId(petitionId)) {
            throw new DuplicatedTempUrlException();
        }
        String generatedUrl = urlGenerator.generate(TEMP_URL_LENGTH);
        while (tempPetitionUrlRepository.existsByTempUrlEquals(generatedUrl)) {
            generatedUrl = urlGenerator.generate(TEMP_URL_LENGTH);
        }
        TempPetitionUrl tempPetitionUrl = tempPetitionUrlRepository.save(new TempPetitionUrl(petitionId, generatedUrl));
        return tempPetitionUrl.getTempUrl();
    }

    public Long findPetitionIdByTempUrl(String tempUrl) {
        TempPetitionUrl tempPetitionUrl = tempPetitionUrlRepository.findByTempUrl(tempUrl)
                .orElseThrow(NoSuchTempUrlException::new);
        return tempPetitionUrl.getPetitionId();
    }
}
