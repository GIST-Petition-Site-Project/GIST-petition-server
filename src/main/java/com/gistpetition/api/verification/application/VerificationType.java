package com.gistpetition.api.verification.application;

public enum VerificationType {
    SignUp("[지스트 청원] 회원 가입 인증 메일", "sign_up_verification.html"),
    NewPassword("[지스트 청원] 비밀번호 찾기 인증 메일", "find_password_verification.html");

    private final String subject;
    private final String template;


    VerificationType(String subject, String template) {
        this.subject = subject;
        this.template = template;
    }

    public String getSubject() {
        return subject;
    }

    public String getTemplate() {
        return template;
    }
}
