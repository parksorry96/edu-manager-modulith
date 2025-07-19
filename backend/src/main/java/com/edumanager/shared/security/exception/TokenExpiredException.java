package com.edumanager.shared.security.exception;


import com.edumanager.shared.exception.ErrorCode;

// 토큰 만료시의 예외
public class TokenExpiredException extends JwtAuthenticationException{

//    기본생성자
    public TokenExpiredException() {
        super(ErrorCode.TOKEN_EXPIRED);
    }
//    로깅용으로만 사용, 클라이언트에게는 노출 x
    public TokenExpiredException(String detail) {
        super(ErrorCode.TOKEN_EXPIRED, detail);
    }
}
