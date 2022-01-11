package com.example.gistcompetitioncnserver.user;

public interface LoginService {

    void login(SignInRequest request);

    void logout();

    LoginUser getLoginUser();
}
