package com.edumanager.user.application.dto.response;

import com.edumanager.shared.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "사용자 회원가입 응답")
public record UserSignupResponse(

        @Schema(description = "사용자 ID", example = "1")
        Long userId,
        
        @Schema(description = "이메일 주소", example = "student@example.com")
        String email,
        
        @Schema(description = "이름", example = "홍길동")
        String name,
        
        @Schema(description = "사용자 역할", example = "STUDENT")
        UserRole role,
        
        @Schema(description = "학원 ID", example = "1")
        Long academyId,
        
        @Schema(description = "이메일 인증 여부", example = "false")
        boolean emailVerified,
        
        @Schema(description = "전화번호 인증 여부", example = "false")
        boolean phoneVerified,
        
        @Schema(description = "계정 생성일시", example = "2025-07-20T00:00:00")
        LocalDateTime createdAt
) {


}
