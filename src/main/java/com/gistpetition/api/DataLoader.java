package com.gistpetition.api;

import com.gistpetition.api.answer.domain.AnswerRepository;
import com.gistpetition.api.petition.application.PetitionService;
import com.gistpetition.api.petition.domain.AgreementRepository;
import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.Petition;
import com.gistpetition.api.petition.domain.PetitionRepository;
import com.gistpetition.api.petition.dto.AgreementRequest;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import com.gistpetition.api.utils.password.BcryptEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Profile("dev")
@RequiredArgsConstructor
@Component
public class DataLoader implements CommandLineRunner {
    public static final String PASSWORD = new BcryptEncoder().hashPassword("test1234!");
    private static final String CONTENT = "KBS에서 방영 중인 드라마 ‘태종 이방원’에서 말을 학대하는 장면이 방영되어 논란이 되고 있습니다. 동물자유연대가 공개한 영상에 따르면 액션 배우가 말을 타고 가는 도중 낙마를 하는 장면에서 말의 발목에 묶어놓은 와이어를 잡아 당겨 말을 강제로 넘어뜨리는 장면이 명확히 찍혀있습니다.";
    public static final AgreementRequest AGREEMENT_REQUEST = new AgreementRequest("동의합니다");

    private final UserRepository userRepository;
    private final PetitionRepository petitionRepository;
    private final AnswerRepository answerRepository;
    private final PetitionService petitionService;
    private final AgreementRepository agreementRepository;

    @Override
    public void run(String... args) {
        userRepository.save(new User("admin@gist.ac.kr", PASSWORD, UserRole.ADMIN));
    }

    @Transactional
    public void loadData() {
        answerRepository.deleteAllInBatch();
        agreementRepository.deleteAllInBatch();
        petitionRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        userRepository.save(new User("admin@gist.ac.kr", PASSWORD, UserRole.ADMIN));
        userRepository.save(new User("manager@gist.ac.kr", PASSWORD, UserRole.MANAGER));
        User user = userRepository.save(new User("user@gist.ac.kr", PASSWORD, UserRole.USER));

        ArrayList<User> alphabetUsers = new ArrayList<>();
        for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) {
            alphabetUsers.add(userRepository.save(new User(alphabet + "@gist.ac.kr", PASSWORD, UserRole.USER)));
        }

        Petition petition1 = petitionRepository.save(new Petition("국민 청원에 글을 써버렸다.", CONTENT, Category.DORMITORY, user.getId()));
        Petition petition2 = petitionRepository.save(new Petition("방송 촬영을 위해 안전과 생존을 위협당하는 동물의 대책 마련이 필요합니다", CONTENT, Category.DORMITORY, user.getId()));
        Petition petition3 = petitionRepository.save(new Petition("길고양이를 학대하는 갤러리를 폐쇄하고 엄중한 수사를 해주십시오.", CONTENT, Category.DORMITORY, user.getId()));
        Petition petition4 = petitionRepository.save(new Petition("코로나19로 부터 우리 아이들을 지켜주세요.", CONTENT, Category.DORMITORY, user.getId()));
        Petition petition5 = petitionRepository.save(new Petition("2차방역지원금 지금 자영업분들 농락하시나요?", CONTENT, Category.DORMITORY, user.getId()));
        List<Petition> petitions = Arrays.asList(petition1, petition2, petition3, petition4, petition5);

        for (Petition petition : petitions) {
            for (User alphabetUser : alphabetUsers) {
                petitionService.agree(AGREEMENT_REQUEST, petition.getId(), alphabetUser.getId());
            }
        }
    }
}
