package com.edumanager.shared.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    //에러 코드
    private final ErrorCode errorCode;

    //에러에 대한 추가 정보
    private final String detail;


    //에러코드로 예외를 생성하는 생성자
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detail = null;
    }

    //코드와 상세 정보를 함께 제공하는 생성자
    public BusinessException(ErrorCode errorCode, String detail) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detail = detail;
    }

    //에러 코드와 원인이 된 예외를 함께 제공하는 생성자
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.detail = null;
    }
}