package com.edumanager.user.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답")
public record LoginResponse(
        @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,
        
        @Schema(description = "토큰 타입", example = "Bearer")
        String tokenType,
        
        @Schema(description = "토큰 만료 시간 (초)", example = "3600")
        long expiresIn,
        
        @Schema(description = "사용자 정보")
        UserSignupResponse user
) {
    public static LoginResponse of(String accessToken, long expiresIn, UserSignupResponse user) {
        return new LoginResponse(accessToken, "Bearer", expiresIn, user);
    }
}
