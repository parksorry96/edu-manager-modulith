package com.edumanager.shared.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    /* ==================== Common Errors (C로 시작) ==================== */
    // 잘못된 입력값에 대한 일반적인 에러 (400 Bad Request)
    INVALID_INPUT_VALUE(400, "C001", "유효하지 않은 입력값입니다"),

    // 지원하지 않는 HTTP 메서드 요청 시 (405 Method Not Allowed)
    METHOD_NOT_ALLOWED(405, "C002", "지원하지 않는 HTTP 메서드입니다"),

    // 요청한 리소스를 찾을 수 없을 때 (404 Not Found)
    ENTITY_NOT_FOUND(404, "C003", "엔티티를 찾을 수 없습니다"),

    // 서버 내부 오류 (500 Internal Server Error)
    INTERNAL_SERVER_ERROR(500, "C004", "서버 오류가 발생했습니다"),

    // 잘못된 타입의 값이 입력되었을 때 (400 Bad Request)
    INVALID_TYPE_VALUE(400, "C005", "유효하지 않은 타입입니다"),

    // 권한이 없는 리소스 접근 시 (403 Forbidden)
    ACCESS_DENIED(403, "C006", "접근이 거부되었습니다"),

    /* ==================== Authentication Errors (A로 시작) ==================== */
    // 로그인 실패 시 (401 Unauthorized)
    AUTHENTICATION_FAILED(401, "A001", "인증에 실패했습니다"),

    // JWT 토큰 만료 시 (401 Unauthorized)
    TOKEN_EXPIRED(401, "A002", "토큰이 만료되었습니다"),

    // 유효하지 않은 JWT 토큰 (401 Unauthorized)
    INVALID_TOKEN(401, "A003", "유효하지 않은 토큰입니다"),

    // 리프레시 토큰을 찾을 수 없을 때 (404 Not Found)
    REFRESH_TOKEN_NOT_FOUND(404, "A004", "리프레시 토큰을 찾을 수 없습니다"),

    // 인증되지 않은 사용자의 접근 (401 Unauthorized)
    UNAUTHORIZED(401, "A005", "인증되지 않은 사용자입니다"),


    /* ==================== Student Domain Errors (S로 시작) ==================== */
    // 학생을 찾을 수 없을 때 (404 Not Found)
    STUDENT_NOT_FOUND(404, "S001", "학생을 찾을 수 없습니다"),

    // 학생 정보 중복 시 (409 Conflict)
    DUPLICATE_STUDENT(409, "S002", "이미 등록된 학생입니다"),

    // 유효하지 않은 학생 상태 (400 Bad Request)
    INVALID_STUDENT_STATUS(400, "S003", "유효하지 않은 학생 상태입니다"),

    /* ==================== Course Domain Errors (CO로 시작) ==================== */
    // 수업을 찾을 수 없을 때 (404 Not Found)
    COURSE_NOT_FOUND(404, "CO001", "수업을 찾을 수 없습니다"),

    // 수업 정원 초과 시 (409 Conflict)
    COURSE_FULL(409, "CO002", "수업 정원이 초과되었습니다"),

    // 유효하지 않은 수업 일정 (400 Bad Request)
    INVALID_SCHEDULE(400, "CO003", "유효하지 않은 수업 일정입니다"),

    // 이미 등록된 수업 (409 Conflict)
    DUPLICATE_ENROLLMENT(409, "CO004", "이미 등록된 수업입니다"),

    /* ==================== Attendance Domain Errors (AT로 시작) ==================== */
    // 출석 정보를 찾을 수 없을 때 (404 Not Found)
    ATTENDANCE_NOT_FOUND(404, "AT001", "출석 정보를 찾을 수 없습니다"),

    // 중복 출석 체크 시 (409 Conflict)
    DUPLICATE_ATTENDANCE(409, "AT002", "이미 출석 체크가 되었습니다"),

    // 출석 체크 가능 시간이 아닐 때 (400 Bad Request)
    INVALID_ATTENDANCE_TIME(400, "AT003", "출석 체크 시간이 아닙니다"),

    /* ==================== Revenue Domain Errors (R로 시작) ==================== */
    // 결제 정보를 찾을 수 없을 때 (404 Not Found)
    PAYMENT_NOT_FOUND(404, "R001", "결제 정보를 찾을 수 없습니다"),

    // 잔액 부족 시 (400 Bad Request)
    INSUFFICIENT_BALANCE(400, "R002", "잔액이 부족합니다"),

    // 이미 처리된 결제 (409 Conflict)
    PAYMENT_ALREADY_PROCESSED(409, "R003", "이미 처리된 결제입니다"),

    // 유효하지 않은 결제 금액 (400 Bad Request)
    INVALID_PAYMENT_AMOUNT(400, "R004", "유효하지 않은 결제 금액입니다"),

    /* ==================== External Service Errors (E로 시작) ==================== */
    // SMS 전송 실패 (500 Internal Server Error)
    SMS_SEND_FAILED(500, "E001", "SMS 전송에 실패했습니다"),

    // 이메일 전송 실패 (500 Internal Server Error)
    EMAIL_SEND_FAILED(500, "E002", "이메일 전송에 실패했습니다"),

    // 외부 API 호출 실패 (500 Internal Server Error)
    EXTERNAL_API_ERROR(500, "E003", "외부 API 호출 중 오류가 발생했습니다");

    // HTTP 상태 코드를 저장하는 필드
    private final int status;

    // 우리가 정의한 커스텀 에러 코드 (도메인별로 구분하기 쉽게)
    private final String code;

    // 사용자에게 보여줄 에러 메시지
    private final String message;
}