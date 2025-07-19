package com.edumanager.shared.exception;

import com.edumanager.shared.dto.response.ErrorResponse;
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

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("BusinessException : {}", e.getMessage(), e);

        ErrorCode errorCode = e.getErrorCode();

//        에러 응답 생성(detail이 있으면 포함)
        ErrorResponse response = ErrorResponse.of(errorCode, e.getDetail());

//       Http 상태 코드와 함께 응답 변환
        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException : {}", e.getMessage(), e);

        // BindingResult 에서 필드별 에러 정보를 추출하여 응답 생성
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        log.error("BindException : {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, (BindingResult) e);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    //  지원하지 않는 HTTP 메소드 요청 시 예외 처리
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException : {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED);

        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    //    security 접근 거부 예외 처리
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error("AccessDeniedException: {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.of(ErrorCode.ACCESS_DENIED);

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    //  DB 제약조건 위반 시 발생하는 예외 처리
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException: {}", e.getMessage(), e);

        // 상세 메시지와 함께 응답
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE,
                "데이터 무결성 위반");

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //  Http 본문을 읽을 수 없을 때 예외처리
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException: {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_TYPE_VALUE,
                "요청 본문을 읽을 수 없습니다");

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    //  위에서 잡지 못한 예외 처리, 500에러 반환
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        // 예상치 못한 에러는 자세한 스택 트레이스와 함께 로그
        log.error("Exception: {}", e.getMessage(), e);

        // 클라이언트에게는 일반적인 서버 오류 메시지만 전달
        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
