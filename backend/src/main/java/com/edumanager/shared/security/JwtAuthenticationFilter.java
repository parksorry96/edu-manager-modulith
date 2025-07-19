package com.edumanager.shared.security;


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

@Slf4j
@RequiredArgsConstructor
@Component
//한 번만 실행되는 필터
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    //JWT 토큰 관련 작업을 처리
    private final JwtTokenProvider jwtTokenProvider;

    // 사용자가 정보를 조회하기 위한 레포지토리
    private final UserRepository userRepository;

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

                // 3. 토큰에서 이메일 추출
                String email= jwtTokenProvider.getEmailFromToken(token);
                // Claims 로 더 많은 정보 추출
                Claims claims = jwtTokenProvider.getClaims(token);

                Long userId = claims.get("userId", Long.class);
                String role = claims.get("role", String.class);

                // 4. 사용자 조회
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new InvalidTokenException("사용자를 찾을 수 없습니다."));

                // 5. 인증 객체 생성 및 SecurityContext 설정
                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        }catch(Exception e){
            log.error("JWT 인증 과정에서 오류 발생 : {}", e.getMessage());
            SecurityContextHolder.clearContext();
            request.setAttribute("jwt-error",e);
        }

        filterChain.doFilter(request, response);
    }
}
