package com.edumanager.user.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "학생 회원가입 요청")
public record StudentSignupRequest(

        @Schema(description = "초대코드", example = "INVITE01", required = true)
        @NotBlank(message = "초대코드는 필수입니다")
        String inviteCode,

        @Schema(description = "이메일 주소", example = "student@example.com", required = true)
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,

        @Schema(description = "비밀번호 (최소 8자, 영문+숫자 조합)", example = "password123", required = true)
        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "비밀번호는 영문과 숫자를 포함해야 합니다")
        String password,

        @Schema(description = "이름", example = "홍길동", required = true)
        @NotBlank(message = "이름은 필수입니다")
        @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다")
        String name,

        @Schema(description = "전화번호 (01X-XXXX-XXXX 형식)", example = "010-1234-5678")
        @Pattern(regexp = "^01\\d-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다")
        String phone
) {


}
