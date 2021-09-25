package com.example.gistcompetitioncnserver.registration;

import com.example.gistcompetitioncnserver.registration.emailsender.EmailSender;
import com.example.gistcompetitioncnserver.registration.token.EmailConfirmationToken;
import com.example.gistcompetitioncnserver.registration.token.EmailConfirmationTokenService;
import com.example.gistcompetitioncnserver.user.User;
import com.example.gistcompetitioncnserver.user.UserRole;
import com.example.gistcompetitioncnserver.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private final EmailConfirmationTokenService emailConfirmationTokenService;
    private final EmailSender emailSender;

    public String register(RegistrationRequest request, HttpServletRequest urlRequest){
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                UserRole.USER
        );

        String baseUrl = urlRequest.getRequestURL().toString();
        String link = baseUrl.substring(0, baseUrl.length() - 12 ) + "confirm?token=" + userService.signUpUser(user); // redirect home page in local

        emailSender.send(
                request.getEmail(),
                buildEmail(request.getUsername(), link));
        return user.getId().toString();
    }

    public String resendEmail(User user, HttpServletRequest urlRequest){

        Optional<EmailConfirmationToken> userEmailToken = emailConfirmationTokenService.findEmailTokenByUserId(user.getId()) ;
        emailConfirmationTokenService.deleteToken(userEmailToken.get().getToken()); // delete previous token

        String token = userService.createToken(user);
        String baseUrl = urlRequest.getRequestURL().toString();
        String link = baseUrl.substring(0, baseUrl.length() - 12 ) + "confirm?token=" + token; // redirect home page in local

        emailSender.send(
                user.getEmail(),
                buildEmail(user.getUsername(), link));
        return "email is resent"; // redirect로 변경 -> 홈페이지 들어가도록 그리고 alert가 나올 수 있도록
    }



    @Transactional
    public String confirmToken(String token, String email) {
        emailConfirmationTokenService.setConfirmedAt(token);
        userService.enableAppUser(email);

        return "https://gist-petition-web-qtmha8hh2-betterit.vercel.app"; // redirect to the page
    }

    private String buildEmail(String name, String link) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <title>이메일인증</title>\n" +
                "    <meta charset=\"utf-8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "    <style>\n" +
                "        .title{\n" +
                "            padding: 20px 0px; \n" +
                "            border-bottom: 1px solid rgb(223, 223, 223); \n" +
                "            font-size: 20px;\n" +
                "        }\n" +
                "        .table-overall{\n" +
                "            font-size: 16px; \n" +
                "            color: rgb(34, 34, 34); \n" +
                "            line-height: 1.2;\n" +
                "        }\n" +
                "        .line-table{\n" +
                "            height: 1px; \n" +
                "            background: black; \n" +
                "            position: relative;\n" +
                "        }\n" +
                "        .line-content{\n" +
                "            width: 20%; \n" +
                "            height: 1px;    \n" +
                "        }\n" +
                "        .content{\n" +
                "            font-size: 16px;\n" +
                "            padding:10px;\n" +
                "            line-height: 160%;\n" +
                "        }    \n" +
                "        .footer{\n" +
                "            font-size: 13px; \n" +
                "            line-height: 26px; \n" +
                "            padding:15px; \n" +
                "            padding-top:0px;\n" +
                "        }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "      <div>\n" +
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"font-family: NanumSquare, sans-serif;\">\n" +
                "            <tbody><tr>\n" +
                "                <td align=\"center\" style=\"padding: 25px 18px;\">\n" +
                "                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"400px\" class=\"table-overall\">\n" +
                "                    <tbody>\n" +
                "                        <tr>\n" +
                "                            <td align=\"center\" style=\"padding: 25px 0px;\">\n" +
                "                                <img src=\"https://www.gist.ac.kr/en/img/sub01/01030301_logo.jpg\" \n" +
                "                                    style=\"display: block; border: 0px; width:200px\"\n" +
                "                                />\n" +
                "                            </td>\n" +
                "                            </tr>\n" +
                "                        <tr>\n" +
                "                            <td class=\"line-table\">\n" +
                "                                <div class=\"line-content\"></div>\n" +
                "                            </td>\n" +
                "                        </tr>\n" +
                "                        <tr>\n" +
                "                            <td align=\"center\" class=\"title\" >\n" +
                "                                <b style=\"color: rgb(255, 52, 0);\">이메일 인증</b>을 완료해 주세요\n" +
                "                            </td>\n" +
                "                        </tr>\n" +
                "                        <tr>\n" +
                "                            <td class=\"line-table\">\n" +
                "                                <div class=\"line-content\"></div>\n" +
                "                            </td>\n" +
                "                        </tr>\n" +
                "                    \n" +
                "                        <tr>\n" +
                "                            <td style=\"line-height: 20px; padding: 23px 0px;\">\n" +
                "                                <p class=\"content\" style=\" margin: 0px 0px 20px;\">\n" +
                "                                    안녕하세요 " + name + "님,<br>\n" +
                "                                    ShouTInG 회원가입 완료를 위한 메일이 발송되었습니다.\n" +
                "                                </p>\n" +
                "                                <p class=\"content\" style=\"margin: 0px; padding-top:0; \">\n" +
                "                                    ShouTInG 회원가입을 위해 이메일 인증이 필요합니다.\n" +
                "                                    <br>\n" +
                "                                    <span style=\"color: rgb(255, 52, 0);\">\n" +
                "                                        아래 링크를 클릭하여 이메일 인증을 완료해 주세요.\n" +
                "                                    </span> \n" +
                "                                    <br>\n" +
                "                                    <div style=\"padding:10px\">\n" +
                "                                        <a href="+ link + ">링크를 클릭하시면 가입이 완료됩니다</a>\n" +
                "                                    </div>\n" +
                "                                    <div style=\"padding:10px; line-height: 160%;\">\n" +
                "                                        이메일 인증은 메일이 발송된 시점부터 15분 간만 유효합니다.\n" +
                "                                    </div>\n" +
                "                                </p>\n" +
                "                            </td>\n" +
                "                        </tr> \n" +
                "                        <tr>\n" +
                "                            <td class=\"line-table\">\n" +
                "                                <div class=\"line-content\"></div>\n" +
                "                            </td>\n" +
                "                        </tr>\n" +
                "                        <tr>\n" +
                "                            <td style=\"padding-top: 25px; padding-bottom: 24px; border-top: 1px solid black;\">\n" +
                "                                <div class=\"footer\">\n" +
                "                                    본 메일은 발신전용입니다. 더 궁금한 사항은 ShouTInG 오픈카톡을\n" +
                "                                    <br> 이용해주시기 바랍니다.\n" +
                "                                </div>\n" +
                "                                <div><img\n" +
                "                                    src=\"https://giai.gist.ac.kr/thumbnail/popupzoneDetail/PHO_201908061057017980.jpg\"\n" +
                "                                    style=\"display: block; border: 0px; width: 150px\"\n" +
                "                                />   \n" +
                "                                </div>     \n" +
                "                            </td>\n" +
                "                        </tr></tbody>\n" +
                "                    </table>\n" +
                "                </td>\n" +
                "            </tr></tbody>\n" +
                "        </table>\n" +
                "    </div>\n" +
                "  </body>\n" +
                "</html>\n";
    }



}
