package com.edumanager.shared.security;


import com.edumanager.shared.domain.enums.UserRole;
import com.edumanager.shared.security.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
//한 번만 실행되는 필터
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    //JWT 토큰 관련 작업을 처리
    private final JwtTokenProvider jwtTokenProvider;


    // OncePerRequestFilter 의 추상 메서드 구현
    // 모든 HTTP 요청마다 한 번씩 실행되는 필터 메소드
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // JWT 인증 과정에서 발생할 수 있는 모든 예외를 처리하기 위한 try-catch
        try {
            // 1. HTTP 요청의 Authorization 헤더에서 JWT 토큰 추출
            String token = jwtTokenProvider.resolveToken(request);

            // 2. 토큰 검증
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

                // Claims 로 더 많은 정보 추출
                Claims claims = jwtTokenProvider.getClaims(token);

                Long userId = claims.get("userId", Long.class);
                String username = claims.getSubject();
                String name = claims.get("name", String.class);
                String roleString = claims.get("role", String.class);

                UserRole role;
                try{
                    role= UserRole.valueOf(roleString);
                }catch (IllegalArgumentException e){
                    log.error("유효하지 않은 사용자 : {}", roleString);
                    throw new InvalidTokenException("유효하지 않은 사용자 역할입니다.");
                }

                //db조회 없이 객체생성
                CustomUserPrincipal userPrincipal = new CustomUserPrincipal(userId, username, name, role);

                // 5. 인증 객체 생성 및 SecurityContext 설정
                Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("JWT 인증성공 username : {}", username);
            }
        }catch(Exception e){
            log.error("JWT 인증 과정에서 오류 발생 : {}", e.getMessage());
            SecurityContextHolder.clearContext();
            request.setAttribute("jwt-error",e);
        }

        filterChain.doFilter(request, response);
    }
}
