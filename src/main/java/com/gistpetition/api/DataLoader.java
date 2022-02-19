package com.gistpetition.api;

import com.gistpetition.api.answer.application.AnswerService;
import com.gistpetition.api.answer.domain.AnswerRepository;
import com.gistpetition.api.answer.dto.AnswerRequest;
import com.gistpetition.api.petition.application.PetitionService;
import com.gistpetition.api.petition.domain.AgreementRepository;
import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.PetitionRepository;
import com.gistpetition.api.petition.dto.AgreementRequest;
import com.gistpetition.api.petition.dto.PetitionRequest;
import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRepository;
import com.gistpetition.api.user.domain.UserRole;
import com.gistpetition.api.utils.password.BcryptEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.gistpetition.api.petition.domain.Petition.REQUIRED_AGREEMENT;

@Profile("dev")
@RequiredArgsConstructor
@Component
public class DataLoader {
    public static final String PASSWORD = new BcryptEncoder().hashPassword("test1234!");
    private static final String CONTENT = "해당 드라마는 방영 전 이미 시놉시스 공개로 한차례 민주화운동을 폄훼하는 내용으로 큰 논란이 된 바 있으며 20만명 이상의 국민들이 해당 드라마의 방영 중지 청원에 동의하였습니다. 당시 제작진은 전혀 그럴 의도가 없으며 “남녀 주인공이 민주화 운동에 참여하거나 이끄는 설정은 대본 어디에도 존재하지 않는다.” 라고 주장했습니다. 그러나 1화가 방영된 현재 드라마에서 여주인공은 간첩인 남주인공을 운동권으로 오인해 구해주었습니다.\n" +
            "\n" +
            "민주화운동 당시 근거없이 간첩으로 몰려서 고문을 당하고 사망한 운동권 피해자들이 분명히 존재하며 이러한 역사적 사실에도 불구하고 저런 내용의 드라마를 만든 것은 분명히 민주화운동의 가치를 훼손시키는 일이라고 생각합니다. 뿐만 아니라 간첩인 남자주인공이 도망가며, 안기부인 서브 남주인공이 쫓아갈 때 배경음악으로 ‘솔아 푸르른 솔아’ 가 나왔습니다. 이 노래는 민주화운동 당시 학생운동 때 사용되었던 노래이며 민주화운동을 수행하는 사람들의 고통과 승리를 역설하는 노래입니다. 그런 노래를 1980년대 안기부를 연기한 사람과 간첩을 연기하는 사람의 배경음악으로 사용한 것 자체가 용인될 수 없는 행위입니다. 뿐만 아니라 해당 드라마는 ott서비스를 통해 세계 각 국에서 시청할 수 있으며 다수의 외국인에게 민주화운동에 대한 잘못된 역사관을 심어줄 수 있기에 더욱 방영을 강행해서는 안 된다고 생각합니다.\n" +
            "\n" +
            "한국은 엄연한 민주주의 국가이며 이러한 민주주의는 노력없이 이루어진 것이 아닌, 결백한 다수의 고통과 희생을 통해 쟁취한 것입니다. 이로부터 고작 약 30년이 지난 지금, 민주화운동의 가치를 훼손하는 드라마의 방영은 당연히 중지되어야 하며 한국문화의 영향력이 점차 커지고 있는 현 시점에서 방송계 역시 역사왜곡의 심각성에 대해 다시 한번 생각해 봤으면 합니다.\n";

    private static final String ANSWER_CONTENT = "<드라마 설** 방영 중지> 관련 국민청원에 답합니다.\n" +
            "\n" +
            "청원인께서는 드라마의 일부 내용과 설정들이 민주화 운동의 가치를 훼손시키고 있고, 시청자들에게 잘못된 역사관을 심어줄 수 있다며 방영 중지를 요구하셨습니다. 청원에는 약 36만5천여 명의 국민께서 동의해 주셨습니다.\n" +
            "\n" +
            "드라마 방영사인 JTBC는 작년 12월 입장문을 통해 “‘역사왜곡’과 ‘민주화운동 폄훼’에 대한 우려는 향후 드라마 전개 과정에서 오해의 대부분이 해소될 것”이라고 밝혔고, 해당 드라마는 지난달 30일 16부로 종영되었습니다.\n" +
            "\n" +
            "「방송법」제4조는 방송 편성의 자유와 독립을 보장하면서, 법률에 의하지 않은 규제나 간섭을 할 수 없도록 규정하고 있습니다.\n" +
            "\n" +
            "때문에 정부는 국민 정서에 반하는 창작물의 내용에 대해 창작자, 제작자, 수용자 등 민간에서 이뤄지는 자정 노력 및 자율적 선택을 존중한다는 점을 이전의 방송 중지 요청 청원에서 답변드린 바 있습니다.\n" +
            "\n" +
            "다만 공정성, 공공성 유지 등 방송의 공적책임을 다했는지 여부는 방송통신심의위원회(이하 방심위)에서 심의대상이 됩니다. 방심위에 따르면 드라마 <설**> 관련 접수된 시청자 민원이 900여 건에 달하며, 절차에 따라 방송심의 규정 위반 여부가 논의될 예정입니다.\n" +
            "\n" +
            "방송법은 방송심의규정 위반 시 그 정도에 따라 권고, 의견 제시, 제재 조치(주의, 경고 등)를 규정하고 있으며, 제재 조치를 받을 경우에는 방송통신위원회의 방송 평가 및 방송사 재승인 심사 시 반영됩니다.\n" +
            "\n" +
            "K-콘텐츠가 세계의 주목을 받고 있는 만큼 ‘창작의 자율성’과 ‘방송의 공적책임 준수’ 사이의 균형이 잘 이루어질 수 있도록 노력하겠습니다.\n" +
            "\n" +
            "국민청원에 함께해 주신 국민 여러분께 감사드립니다. ";
    public static final AgreementRequest AGREEMENT_REQUEST = new AgreementRequest("동의합니다");

    private final UserRepository userRepository;
    private final PetitionRepository petitionRepository;
    private final AnswerRepository answerRepository;
    private final PetitionService petitionService;
    private final AnswerService answerService;
    private final AgreementRepository agreementRepository;
    private int anInt;

    @Transactional
    public void loadData() {
        answerRepository.deleteAllInBatch();
        agreementRepository.deleteAllInBatch();
        petitionRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        User admin = userRepository.save(new User("admin@gist.ac.kr", PASSWORD, UserRole.ADMIN));
        User manager = userRepository.save(new User("manager@gist.ac.kr", PASSWORD, UserRole.MANAGER));
        User normal = userRepository.save(new User("user@gist.ac.kr", PASSWORD, UserRole.USER));

        ArrayList<User> alphabetUsers = new ArrayList<>();
        for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) {
            alphabetUsers.add(userRepository.save(new User(alphabet + "@gist.ac.kr", PASSWORD, UserRole.USER)));
        }

        List<Long> petitionIds = List.of(
                savePetition("국민 청원에 글을 써버렸다.", normal),
                savePetition("국민 청원에 글을 써버렸다.", normal),
                savePetition("국민 청원에 글을 써버렸다.", normal),
                savePetition("국민 청원에 글을 써버렸다.", normal),
                savePetition("국민 청원에 글을 써버렸다.", normal),
                savePetition("국민 청원에 글을 써버렸다.", normal),
                savePetition("국민 청원에 글을 써버렸다.", normal),
                savePetition("국민 청원에 글을 써버렸다.", normal),
                savePetition("국민 청원에 글을 써버렸다.", normal),
                savePetition("국민 청원에 글을 써버렸다.", normal),
                savePetition("국민 청원에 글을 써버렸다.", normal),
                savePetition("국민 청원에 글을 써버렸다.", normal),
                savePetition("국민 청원에 글을 써버렸다.", normal),
                savePetition("국민 청원에 글을 써버렸다.", normal),
                savePetition("국민 청원에 글을 써버렸다.", normal),
                savePetition("2차방역지원금 지금 자영업분들 농락하시나요?", normal),
                savePetition("2차방역지원금 지금 자영업분들 농락하시나요?", normal),
                savePetition("2차방역지원금 지금 자영업분들 농락하시나요?", normal),
                savePetition("2차방역지원금 지금 자영업분들 농락하시나요?", normal),
                savePetition("2차방역지원금 지금 자영업분들 농락하시나요?", normal),
                savePetition("2차방역지원금 지금 자영업분들 농락하시나요?", normal),
                savePetition("2차방역지원금 지금 자영업분들 농락하시나요?", normal),
                savePetition("2차방역지원금 지금 자영업분들 농락하시나요?", normal),
                savePetition("2차방역지원금 지금 자영업분들 농락하시나요?", normal),
                savePetition("2차방역지원금 지금 자영업분들 농락하시나요?", normal)
        );
        savePetition("Temp 2차방역지원금 지금 자영업분들 농락하시나요?", normal);
        savePetition("Temp 2차방역지원금 지금 자영업분들 농락하시나요?", normal);
        savePetition("Temp 2차방역지원금 지금 자영업분들 농락하시나요?", normal);

        Random random = new Random();
        int waitingForCheckPetitionCount = 3;
        for (long petitionId = 0; petitionId < petitionIds.size(); petitionId++) {
            int agreeCount = random.nextInt(alphabetUsers.size() - REQUIRED_AGREEMENT) + REQUIRED_AGREEMENT;
            for (int j = 0; j < agreeCount; j++) {
                User user = alphabetUsers.get(j);
                petitionService.agree(AGREEMENT_REQUEST, petitionId, user.getId());
            }
            if (petitionId < waitingForCheckPetitionCount) {
                continue;
            }
            petitionService.releasePetition(petitionId);
            answerService.createAnswer(petitionId, new AnswerRequest(ANSWER_CONTENT));
        }
    }

    private Long savePetition(String title, User user) {
        return petitionService.createPetition(new PetitionRequest(title, CONTENT, Category.DORMITORY.getId()), user.getId());
    }
}
