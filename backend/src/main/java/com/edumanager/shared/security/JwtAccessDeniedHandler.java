package com.edumanager.shared.security;


import com.edumanager.shared.dto.response.ErrorResponse;
import com.edumanager.shared.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        //현재 인증된 사용자 정보 가져오기
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        if(auth !=null && auth.isAuthenticated()){
            log.warn("Access Denied - USER{}(roles:{}), Request URI: {}, Method: {}",
                    auth.getName(),
                    auth.getAuthorities(),
                    request.getRequestURI(),
                    request.getMethod()
            );
        } else{
            log.warn("Access Denied, Request URI :{}", request.getRequestURI());
        }

        // HTTP 응답 설정
        response.setContentType("application/json;charset=UTF-8");  // JSON 형식, UTF-8 인코딩
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);       // 403 상태 코드

        // 에러 응답 생성 및 전송
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.ACCESS_DENIED);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
