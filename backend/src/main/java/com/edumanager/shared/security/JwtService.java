package com.edumanager.shared.security;

import com.edumanager.shared.domain.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration; // 초 단위

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration; // 초 단위

    public String createAccessToken(Long userId, String email, UserRole role) {
        Instant now  = Instant.now();
        Instant expiresAt = now.plusSeconds(accessTokenExpiration);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("edumanager")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(email)
                .claim("userId", userId)
                .claim("role", role.name())
                .claim("type","access")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

    }

    /**
     * 토큰 검증 및 파싱
     */
    public Jwt validateAndParseToken(String token) {
        try {
            return jwtDecoder.decode(token);
        } catch (JwtException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    public Long getUserIdFromToken(String token) {
        Jwt jwt = validateAndParseToken(token);
        return jwt.getClaim("userId");
    }

    /**
     * 토큰 타입 확인
     */
    public boolean isRefreshToken(String token) {
        Jwt jwt = validateAndParseToken(token);
        return "refresh".equals(jwt.getClaim("type"));
    }

    /**
     * Access Token 만료 시간 반환 (초 단위)
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
}
