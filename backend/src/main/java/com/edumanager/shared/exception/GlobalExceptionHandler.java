package com.edumanager.shared.exception;

import com.edumanager.shared.dto.response.ApiResponse;
import com.edumanager.shared.dto.response.FailureResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice   //모든 컨트롤러에서 발생한 예외를 처리
public class GlobalExceptionHandler {

    /**
     * 비즈니스 로직 예외 처리
     * 도메인에서 발생하는 모든 비즈니스 예외를 처리
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage(), e);

        // FailureResponse 유틸리티 사용으로 간소화
        ApiResponse<Void> response = FailureResponse.of(e.getErrorCode(), e.getDetail());

        // ErrorCode에서 HTTP 상태 코드 자동 추출
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(response);
    }

    /**
     * 엔티티를 찾을 수 없는 경우 예외 처리
     * EntityNotFoundException은 BusinessException을 상속하므로 더 구체적으로 처리
     */
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ApiResponse<Void>> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("EntityNotFoundException: {}", e.getMessage(), e);

        // 더 구체적인 FailureResponse 메서드 사용
        ApiResponse<Void> response = FailureResponse.notFound(e.getDetail());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * @Valid 검증 실패 예외 처리
     * 컨트롤러 메서드의 @RequestBody @Valid 검증 실패 시 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage(), e);

        // FailureResponse의 validation 메서드 사용으로 간소화
        ApiResponse<Void> response = FailureResponse.validation(e.getBindingResult());

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * @ModelAttribute 바인딩 실패 예외 처리
     * 폼 데이터 바인딩 시 검증 실패 시 발생
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ApiResponse<Void>> handleBindException(BindException e) {
        log.error("BindException: {}", e.getMessage(), e);

        // BindException은 BindingResult를 상속하므로 직접 전달 가능
        ApiResponse<Void> response = FailureResponse.validation(e.getBindingResult());

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 지원하지 않는 HTTP 메서드 요청 시 예외 처리
     * GET 요청만 지원하는데 POST로 요청하는 경우 등
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException: {}", e.getMessage(), e);

        ApiResponse<Void> response = FailureResponse.of(ErrorCode.METHOD_NOT_ALLOWED,
                String.format("지원하지 않는 HTTP 메서드입니다. 지원 메서드: %s", String.join(", ", e.getSupportedMethods())));

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * Spring Security 접근 거부 예외 처리
     * 인증은 되었지만 권한이 부족한 경우
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("AccessDeniedException: {}", e.getMessage(), e);

        // FailureResponse의 forbidden 메서드 사용
        ApiResponse<Void> response = FailureResponse.forbidden("해당 리소스에 접근할 권한이 없습니다.");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * 데이터베이스 제약조건 위반 예외 처리
     * UNIQUE, NOT NULL, FOREIGN KEY 제약조건 위반 시 발생
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException: {}", e.getMessage(), e);

        // 제약조건 위반 상세 정보 파싱 (선택적)
        String detail = "데이터 무결성 제약조건을 위반했습니다.";
        if (e.getMessage().contains("duplicate key")) {
            detail = "이미 존재하는 데이터입니다.";
        } else if (e.getMessage().contains("not null")) {
            detail = "필수 항목이 누락되었습니다.";
        } else if (e.getMessage().contains("foreign key")) {
            detail = "참조된 데이터를 찾을 수 없습니다.";
        }

        ApiResponse<Void> response = FailureResponse.of(ErrorCode.INVALID_INPUT_VALUE, detail);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * HTTP 요청 본문을 읽을 수 없는 경우 예외 처리
     * JSON 파싱 실패, 잘못된 Content-Type 등
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException: {}", e.getMessage(), e);

        String detail = "요청 본문을 읽을 수 없습니다.";
        if (e.getMessage().contains("JSON parse error")) {
            detail = "JSON 형식이 올바르지 않습니다.";
        } else if (e.getMessage().contains("Required request body is missing")) {
            detail = "요청 본문이 필요합니다.";
        }

        ApiResponse<Void> response = FailureResponse.of(ErrorCode.INVALID_TYPE_VALUE, detail);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 검증 예외 처리 (커스텀 ValidationException)
     * 비즈니스 로직에서 발생하는 검증 오류
     */
    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException e) {
        log.error("ValidationException: {}", e.getMessage(), e);

        // ValidationException에서 FieldError 목록을 가져와서 사용
        if (e.getErrors() != null && !e.getErrors().isEmpty()) {
            // Map<String, String>을 FieldError 리스트로 변환
            var fieldErrors = e.getErrors().entrySet().stream()
                    .map(entry -> new ApiResponse.FieldError(entry.getKey(), "", entry.getValue()))
                    .toList();

            ApiResponse<Void> response = FailureResponse.validation(fieldErrors);
            return ResponseEntity.badRequest().body(response);
        } else {
            ApiResponse<Void> response = FailureResponse.of(e.getErrorCode(), e.getDetail());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 예상치 못한 예외 처리 (최종 안전망)
     * 위에서 처리되지 않은 모든 예외를 캐치
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        // 예상치 못한 에러는 자세한 스택 트레이스와 함께 로그
        log.error("Unexpected Exception: {}", e.getMessage(), e);

        // 클라이언트에게는 일반적인 서버 오류 메시지만 전달 (보안상 이유)
        ApiResponse<Void> response = FailureResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
