package com.edumanager.shared.security;


import com.edumanager.shared.security.exception.InvalidTokenException;
import com.edumanager.shared.security.exception.TokenExpiredException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long tokenValidityInMilliseconds;


    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long tokenValidityInMilliseconds){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;
    }

    public String createToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
                .subject(userDetails.getUsername())         // 토큰 제목 (사용자명)
                .claim("userId", getUserId(userDetails))    // 사용자 ID (별도 메서드로 추출)
                .claim("role", getRole(userDetails))        // 사용자 역할
                .claim("name", getName(userDetails))        // 사용자 실명
                .issuedAt(now)                              // 발급 시간
                .expiration(validity)                       // 만료 시간
                .signWith(key)    //서명  -> JJWT 업그레이드에 따라서 알고리즘 자동선택
                .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)        //서명 검증용 키 설정
                .build()
                .parseSignedClaims(token)//토큰 파싱, 서명 검증 Jws<Claims>
                .getPayload();          // 페이로드 가져오기
        return claims.getSubject();     // 이메일 반환
    }

    // HTTP요청 헤더에서 JWT 토큰을 추출하는 메서드
    public String resolveToken(HttpServletRequest request) {
        //HTTP 요청의 Authorization 헤더값을 가져옴
        String bearerToken = request.getHeader("Authorization");

        //bearer 7글자를 제외하고 실제 jwt 토큰을 변환
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }

        //토큰이 없으면 null 반환
        return null;
    }

    public boolean validateToken(String token) {
        try{
            Jwts.parser()
                    .verifyWith(key)            // 서명검증을 위한 SecretKey 설정
                    .build()                    // JwtParser 객체 생성완료
                    .parseSignedClaims(token);  // 토큰 파싱 및 서명 검증 수행

            // 예외발생 없으면 토큰 유효 -> true 반환
            return true;
        } catch(ExpiredJwtException e) {
            //토큰 시간이 만료 된 경우
            log.error("Expired JWT token : {}", e.getMessage());
            throw new TokenExpiredException("토큰이 만료되었습니다.");
        }catch(UnsupportedJwtException e){
            // 지원되지 않는 JWT 형식인 경우
            log.error("Unsupported JWT token : {}", e.getMessage());
            throw new InvalidTokenException("지원되지 않는 토큰입니다.");
        }catch(MalformedJwtException e){
            // JWT 형식이 올바르지 않는 경우
            log.error("Malformed JWT token : {}", e.getMessage());
            throw new InvalidTokenException("잘못된 형식의 토큰입니다.");
        }catch(SecurityException | IllegalArgumentException e){
            // 서명이 유효하지 않거나 토큰이 null인 경우
            log.error("Security exception: {}", e.getMessage());
            throw new InvalidTokenException("유효하지 않는 토큰입니다.");
        }
    }

    //JWT 토큰에서 Claims(페이로드) 정보를 추출하는 메소드
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * UserDetails에서 사용자 ID 추출
     * CustomUserPrincipal인 경우 userId 반환, 아니면 예외 발생
     */
    private Long getUserId(UserDetails userDetails) {
        if (userDetails instanceof CustomUserPrincipal) {
            return ((CustomUserPrincipal) userDetails).getUserId();
        }
        throw new IllegalArgumentException("지원하지 않는 UserDetails 타입입니다.");
    }

    /**
     * UserDetails에서 사용자 역할 추출
     */
    private String getRole(UserDetails userDetails) {
        if (userDetails instanceof CustomUserPrincipal) {
            return ((CustomUserPrincipal) userDetails).getRole().name();
        }
        throw new IllegalArgumentException("지원하지 않는 UserDetails 타입입니다.");
    }

    /**
     * UserDetails에서 사용자 실명 추출
     */
    private String getName(UserDetails userDetails) {
        if (userDetails instanceof CustomUserPrincipal) {
            return ((CustomUserPrincipal) userDetails).getName();
        }
        throw new IllegalArgumentException("지원하지 않는 UserDetails 타입입니다.");
    }

    public String createAccessToken(Long userId, String email, com.edumanager.shared.domain.enums.UserRole
            role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
                .subject(email)                         // 토큰 제목 (이메일)
                .claim("userId", userId)                // 사용자 ID
                .claim("role", role.name())             // 사용자 역할
                .claim("email", email)                  // 이메일 (중복이지만 명시적으로)
                .issuedAt(now)                          // 발급 시간
                .expiration(validity)                   // 만료 시간
                .signWith(key)                          // 서명
                .compact();
    }

    /**
     * 토큰 만료 시간 반환 (밀리초)
     * @return 토큰 만료 시간
     */
    public long getAccessTokenExpiration() {
        return tokenValidityInMilliseconds;
    }

    /**
     * 토큰에서 사용자 ID 추출
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 토큰에서 사용자 역할 추출
     * @param token JWT 토큰
     * @return 사용자 역할
     */
    public String getRoleFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("role", String.class);
    }

}
