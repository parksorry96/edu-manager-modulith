package com.edumanager.user.application.dto.response;

import com.edumanager.shared.domain.enums.UserRole;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserSignupResponse(

        Long userId,
        String email,
        String name,
        UserRole role,
        Long academyId,
        boolean emailVerified,
        boolean phoneVerified,
        LocalDateTime createdAt
) {


}
