package com.gistpetition.api.verification.application;

public enum VerficationType {
    SignUp("[지스트 청원] 회원 가입 인증 메일", "signup.html"),
    NewPassword("[지스트 청원] 비밀번호 찾기 인증 메일", "find_password_verification.html");

    private final String subject;
    private final String template;


    VerficationType(String subject, String template) {
        this.subject = subject;
        this.template = template;
    }
}
