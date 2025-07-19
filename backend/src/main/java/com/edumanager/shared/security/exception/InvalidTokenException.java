package com.edumanager.shared.security.exception;


import com.edumanager.shared.exception.ErrorCode;

// 토큰이 유효하지 않을 때 발생하는 예외
public class InvalidTokenException extends JwtAuthenticationException{

//    기본 생성자
    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }

    public InvalidTokenException(String detail) {
        super(ErrorCode.INVALID_TOKEN, detail);
    }
}
