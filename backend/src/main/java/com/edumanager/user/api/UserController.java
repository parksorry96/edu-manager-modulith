package com.edumanager.user.api;

import com.edumanager.shared.dto.response.ApiResponse;
import com.edumanager.shared.dto.response.SuccessResponse;
import com.edumanager.user.application.dto.request.StudentSignupRequest;
import com.edumanager.user.application.dto.response.UserSignupResponse;
import com.edumanager.user.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "사용자 관리 API")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "학생 회원가입",
            description = "초대코드를 이용한 학생 회원가입. 특정 학생용 초대코드인 경우 자동으로 Student 계정과 연결됩니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이메일 중복")
            }
    )
    @PostMapping("/signup/student")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserSignupResponse> signupStudent(@Valid @RequestBody StudentSignupRequest request) {
        UserSignupResponse response = userService.registerStudentWithInviteCode(request);

        return SuccessResponse.created(response);
    }

    @Operation(summary = "이메일 중복 확인", description = "회원가입 전 이메일 중복 여부를 확인합니다.")
    @GetMapping("/check-email")
    public ApiResponse<Boolean> checkEmailDuplicate(
            @io.swagger.v3.oas.annotations.Parameter(description = "확인할 이메일 주소", example = "user@example.com")
            @RequestParam String email) {
        log.debug("이메일 중복 확인: email={}", email);

        boolean isExists = userService.isEmailExists(email);

        return SuccessResponse.of(!isExists);  // 사용 가능하면 true, 중복이면 false
    }
}
