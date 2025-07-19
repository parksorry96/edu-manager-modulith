package com.edumanager.shared.dto.response;


import com.edumanager.shared.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//API 에러 응답을 위한 DTO
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class ErrorResponse {

    private String code; //에러코드
    private String message;
    private List<FieldError> errors; //필드별 상세 에러 목록
    private LocalDateTime timestamp; // 에러 발생 시각

//    에러코드만 있는 에러 기본생성자
    private ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.errors = new ArrayList<>();
        this.timestamp = LocalDateTime.now();
    }
//  상세 메시지가 있는 에러 응답 생성자
    private ErrorResponse(ErrorCode errorCode, String detail) {
        this.code = errorCode.getCode();
        this.message = detail != null ? detail : errorCode.getMessage();  // detail이 있으면 사용
        this.errors = new ArrayList<>();
        this.timestamp = LocalDateTime.now();
    }
//  필드 에러 목록이 있는 에러 응답 생성자 @Valid 검증 실패시 사용
    private ErrorResponse(ErrorCode errorCode, List<FieldError> errors) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.errors = errors;                    // 필드별 에러 목록 설정
        this.timestamp = LocalDateTime.now();
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }

    public static ErrorResponse of(ErrorCode errorCode, String detail) {
        return new ErrorResponse(errorCode, detail);
    }

    public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult) {
        return new ErrorResponse(errorCode, FieldError.of(bindingResult));
    }

//    내부클래스
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;    // 에러가 발생한 필드명
        private String value;    // 입력된 값 (문제가 된 값)
        private String reason;   // 에러 이유

        /**
         * 필드 에러 생성자 (private)
         */
        private FieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        /**
         * 단일 필드 에러를 리스트로 반환하는 팩토리 메서드
         */
        public static List<FieldError> of(String field, String value, String reason) {
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(field, value, reason));
            return fieldErrors;
        }

        /**
         * Spring의 BindingResult를 FieldError 리스트로 변환
         * @Valid 검증 실패 시 자동으로 생성된 에러들을 우리 형식으로 변환
         */
        private static List<FieldError> of(BindingResult bindingResult) {
            // Spring의 FieldError 목록을 가져옴
            List<org.springframework.validation.FieldError> fieldErrors =
                    bindingResult.getFieldErrors();

            // Stream API를 사용해 우리 형식의 FieldError로 변환
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),                              // 필드명
                            error.getRejectedValue() == null ?             // 거부된 값
                                    "" : error.getRejectedValue().toString(),  // null이면 빈 문자열
                            error.getDefaultMessage()))                    // 에러 메시지
                    .toList();
        }
    }
}
