package com.edumanager.shared.security;


// Spring Security에서 인증되지 않은 요청 처리
// 401 응답을 우리 형식으로 커스터마이징


import com.edumanager.shared.dto.response.ErrorResponse;
import com.edumanager.shared.exception.ErrorCode;
import com.edumanager.shared.security.exception.InvalidTokenException;
import com.edumanager.shared.security.exception.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

//    인증 실패 시 호출되는 메소드
//    Spring Security의 기본 응답 대신 커스텀 에러 응답 변환
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("Unauthorized error: {}", authException.getMessage());

        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        if(authException.getCause() instanceof TokenExpiredException){
            errorCode = ErrorCode.TOKEN_EXPIRED;
        }else if(authException.getCause() instanceof InvalidTokenException){
            errorCode = ErrorCode.INVALID_TOKEN;
        }

        // HTTP 응답 설정
        response.setContentType("application/json;charset=UTF-8");  // JSON 응답, UTF-8 인코딩
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);    // 401 상태 코드

        // 에러 응답 객체 생성 및 JSON으로 변환하여 응답
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}
