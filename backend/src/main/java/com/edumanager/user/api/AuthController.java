package com.edumanager.user.api;


import com.edumanager.shared.dto.response.ApiResponse;
import com.edumanager.shared.dto.response.SuccessResponse;
import com.edumanager.user.application.dto.request.LoginRequest;
import com.edumanager.user.application.dto.response.LoginResponse;
import com.edumanager.user.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        LoginResponse response = authService.login(request);
        return SuccessResponse.of("로그인이 완료되었습니다.", response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String authorization){
        authService.logout(authorization);
        return SuccessResponse.of("로그아웃이 완료되었습니다.");
    }

}
