package com.edumanager.shared.security.exception;


import com.edumanager.shared.exception.BusinessException;
import com.edumanager.shared.exception.ErrorCode;

public class JwtAuthenticationException extends BusinessException {

    public JwtAuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public JwtAuthenticationException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
