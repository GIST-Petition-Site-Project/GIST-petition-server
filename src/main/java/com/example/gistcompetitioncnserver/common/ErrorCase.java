package com.example.gistcompetitioncnserver.common;

public class ErrorCase {
    public static final String DATABASE_CONNECTION_ERROR = "데이터베이스 등 인터넷 연결에 문제가 있습니다. ";
    public static final String INVAILD_FILED_ERROR = "필수 항목을 입력해주세요. ";
    public static final String BAD_REQUEST_ERROR = "잘못된 경로로 접근했습니다. ";
    public static final String NO_SUCH_VERIFICATION_EMAIL_ERROR = "인증되지 않은 계정입니다. 메일 인증을 진행해주세요. ";
    public static final String NO_SUCH_TOKEN_ERROR = "존재하지 않는 토큰입니다. ";
    public static final String NO_SUCH_USER_ERROR = "존재하지 않는 계정입니다. ";
    public static final String NO_SUCH_POST_ERROR = "존재하지 않는 게시글입니다. ";
    public static final String NO_SUCH_IMAGE_ERROR = "존재하지 않는 이미지입니다. ";
    public static final String NO_SUCH_COMMENT_ERROR = "존재하지 않는 댓글입니다. ";
    public static final String FORBIDDEN_ERROR = "권한이 없습니다. ";
    public static final String EXPIRED_TOKEN_ERROR = "토큰이 만료됐습니다. ";
    public static final String USER_ALREADY_EXIST = "이미 존재하는 이메일입니다. ";
    public static final String INVALID_EMAIL = "유효한 이메일이 아닙니다. gm 혹은 gist 메일을 이용해주세요. ";
}
