package com.example.gistcompetitioncnserver.user.application;

import com.example.gistcompetitioncnserver.user.domain.SimpleUser;
import com.example.gistcompetitioncnserver.user.dto.request.SignInRequest;

public interface LoginService {
    void login(SignInRequest request);

    void logout();

    SimpleUser getLoginUser();
}
