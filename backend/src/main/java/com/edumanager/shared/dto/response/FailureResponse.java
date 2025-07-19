package com.edumanager.shared.dto.response;

import com.edumanager.shared.exception.ErrorCode;
import org.springframework.validation.BindingResult;

import java.util.List;

public class FailureResponse {

    // ========== 기본 실패 응답 ==========

    // ErrorCode로 실패 응답
    public static <T> ApiResponse<T> of(ErrorCode errorCode) {
        return ApiResponse.failure(errorCode);
    }

    // ErrorCode + 상세 메시지로 실패 응답
    public static <T> ApiResponse<T> of(ErrorCode errorCode, String detail) {
        return ApiResponse.failure(errorCode, detail);
    }

    // 단순 메시지만으로 실패 응답
    public static <T> ApiResponse<T> of(String message) {
        return ApiResponse.failure(message);
    }

    // ========== 검증 실패 응답 ==========

    // BindingResult로 검증 실패 응답 (@Valid 에러)
    public static <T> ApiResponse<T> validation(BindingResult bindingResult) {
        return ApiResponse.validationFailure(ErrorCode.INVALID_INPUT_VALUE, bindingResult);
    }

    // 커스텀 ErrorCode와 BindingResult로 검증 실패 응답
    public static <T> ApiResponse<T> validation(ErrorCode errorCode, BindingResult bindingResult) {
        return ApiResponse.validationFailure(errorCode, bindingResult);
    }

    // 직접 FieldError 리스트로 검증 실패 응답
    public static <T> ApiResponse<T> validation(List<ApiResponse.FieldError> fieldErrors) {
        return ApiResponse.failure(ErrorCode.INVALID_INPUT_VALUE, fieldErrors);
    }

    // ========== 도메인별 특화 실패 응답 ==========

    // 엔티티를 찾을 수 없는 경우
    public static <T> ApiResponse<T> notFound(String entityName) {
        return ApiResponse.failure(ErrorCode.ENTITY_NOT_FOUND,
                entityName + "을(를) 찾을 수 없습니다.");
    }

    // ID로 엔티티를 찾을 수 없는 경우
    public static <T> ApiResponse<T> notFound(String entityName, Long id) {
        return ApiResponse.failure(ErrorCode.ENTITY_NOT_FOUND,
                String.format("%s을(를) 찾을 수 없습니다. ID: %d", entityName, id));
    }

    // 식별자로 엔티티를 찾을 수 없는 경우
    public static <T> ApiResponse<T> notFound(String entityName, String identifier) {
        return ApiResponse.failure(ErrorCode.ENTITY_NOT_FOUND,
                String.format("%s을(를) 찾을 수 없습니다. 식별자: %s", entityName, identifier));
    }

    // ========== 인증/권한 관련 실패 응답 ==========

    // 인증 실패
    public static <T> ApiResponse<T> unauthorized() {
        return ApiResponse.failure(ErrorCode.UNAUTHORIZED);
    }

    // 인증 실패 + 상세 메시지
    public static <T> ApiResponse<T> unauthorized(String detail) {
        return ApiResponse.failure(ErrorCode.UNAUTHORIZED, detail);
    }

    // 권한 부족
    public static <T> ApiResponse<T> forbidden() {
        return ApiResponse.failure(ErrorCode.ACCESS_DENIED);
    }

    // 권한 부족 + 상세 메시지
    public static <T> ApiResponse<T> forbidden(String detail) {
        return ApiResponse.failure(ErrorCode.ACCESS_DENIED, detail);
    }

    // ========== 학원 도메인 특화 실패 응답 ==========

    // 학생 관련 에러
    public static <T> ApiResponse<T> studentNotFound(Long studentId) {
        return notFound("학생", studentId);
    }

    public static <T> ApiResponse<T> studentAlreadyEnrolled(String courseName) {
        return ApiResponse.failure(ErrorCode.DUPLICATE_ENROLLMENT,
                courseName + " 강좌에 이미 등록된 학생입니다.");
    }

    // 강좌 관련 에러
    public static <T> ApiResponse<T> courseNotFound(Long courseId) {
        return notFound("강좌", courseId);
    }

    public static <T> ApiResponse<T> courseFull(String courseName) {
        return ApiResponse.failure(ErrorCode.COURSE_FULL,
                courseName + " 강좌의 정원이 마감되었습니다.");
    }

    // 결제 관련 에러
//    public static <T> ApiResponse<T> paymentFailed(String reason) {
//        return ApiResponse.failure(ErrorCode.PAYMENT_FAILED,
//                "결제에 실패했습니다: " + reason);
//    }

    public static <T> ApiResponse<T> insufficientBalance(Long amount) {
        return ApiResponse.failure(ErrorCode.INSUFFICIENT_BALANCE,
                String.format("잔액이 부족합니다. 필요 금액: %,d원", amount));
    }

    // ========== 비즈니스 로직 에러 ==========

    // 중복 리소스
    public static <T> ApiResponse<T> alreadyExists(String resourceName) {
        return ApiResponse.failure(ErrorCode.INVALID_INPUT_VALUE,
                "이미 존재하는 " + resourceName + "입니다.");
    }

    // 비즈니스 규칙 위반
    public static <T> ApiResponse<T> businessRuleViolation(String rule) {
        return ApiResponse.failure(ErrorCode.INVALID_INPUT_VALUE,
                "비즈니스 규칙 위반: " + rule);
    }

    // 외부 서비스 에러
    public static <T> ApiResponse<T> externalServiceError(String serviceName) {
        return ApiResponse.failure(ErrorCode.EXTERNAL_API_ERROR,
                serviceName + " 서비스 연동 중 오류가 발생했습니다.");
    }
}
