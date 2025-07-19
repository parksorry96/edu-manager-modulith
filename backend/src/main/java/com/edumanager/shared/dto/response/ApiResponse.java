package com.edumanager.shared.dto.response;

import com.beust.ah.A;
import com.edumanager.shared.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.List;


public record ApiResponse<T>(
        boolean success,
        String message,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        T data,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        List<FieldError> fieldErrors,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp

) {

    // ========== 성공 응답들 ==========
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", data, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, null, LocalDateTime.now());
    }

    // ========== 실패 응답들 ==========
    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(false, message, null, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> failure(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getMessage(), null, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> failure(ErrorCode errorCode, String detail) {
        String message = detail != null ? detail : errorCode.getMessage();
        return new ApiResponse<>(false, message, null, null, LocalDateTime.now());
    }

    // 검증 실패 응답 (@Valid 에러)
    public static <T> ApiResponse<T> validationFailure(ErrorCode errorCode, BindingResult bindingResult) {
        return new ApiResponse<>(false, errorCode.getMessage(), null, FieldError.of(bindingResult), LocalDateTime.now());
    }

    // 필드 에러와 함께 실패 응답
    public static <T> ApiResponse<T> failure(ErrorCode errorCode, List<FieldError> fieldErrors) {
        return new ApiResponse<>(false, errorCode.getMessage(), null, fieldErrors, LocalDateTime.now());
    }

    /**
     * 필드 에러 Record
     */
    public record FieldError(
            String field,
            String value,
            String reason
    ) {
        public static List<FieldError> of(String field, String value, String reason) {
            return List.of(new FieldError(field, value, reason));
        }

        public static List<FieldError> of(BindingResult bindingResult) {
            return bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()
                    ))
                    .toList();
        }
    }
}
