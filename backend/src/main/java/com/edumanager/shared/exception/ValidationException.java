package com.edumanager.shared.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class ValidationException extends BusinessException {

    private final Map<String,String> errors;

    //여러 필드의 검증 오류를 한번에 전달
    public ValidationException(Map<String, String> errors) {
        super(ErrorCode.INVALID_INPUT_VALUE);  // 기본 에러 코드 사용
        this.errors = errors;                   // 검증 오류 Map 저장
    }

    // 단일 필드의 검증 오류를 전달
    public ValidationException(String field, String message) {
        super(ErrorCode.INVALID_INPUT_VALUE);   // 기본 에러 코드 사용
        this.errors = Map.of(field, message);   // 단일 항목 Map 생성
    }
}