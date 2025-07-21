package com.edumanager.user.api;


import com.edumanager.shared.dto.response.ApiResponse;
import com.edumanager.shared.dto.response.SuccessResponse;
import com.edumanager.user.application.dto.request.LoginRequest;
import com.edumanager.user.application.dto.response.LoginResponse;
import com.edumanager.user.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "인증 관리 API")
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호를 이용한 사용자 로그인",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
            }
    )
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        LoginResponse response = authService.login(request);
        return SuccessResponse.of("로그인이 완료되었습니다.", response);
    }

    @Operation(
            summary = "로그아웃",
            description = "JWT 토큰을 무효화하여 로그아웃 처리",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")
            }
    )
    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @Parameter(description = "Authorization 헤더 (Bearer 토큰)", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authorization){
        authService.logout(authorization);
        return SuccessResponse.of("로그아웃이 완료되었습니다.");
    }

}
