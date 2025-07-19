package com.edumanager.user.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record StudentSignupRequest(

        @NotBlank(message = "초대코드는 필수입니다")
        String inviteCode,

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "비밀번호는 영문과 숫자를 포함해야 합니다")
        String password,

        @NotBlank(message = "이름은 필수입니다")
        @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다")
        String name,

        @Pattern(regexp = "^01\\d-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다")
        String phone
) {


}
