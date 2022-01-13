package com.gistpetition.api.user.application;

import com.gistpetition.api.user.domain.SimpleUser;
import com.gistpetition.api.user.dto.request.SignInRequest;

public interface LoginService {
    void login(SignInRequest request);

    void logout();

    SimpleUser getLoginUser();
}
