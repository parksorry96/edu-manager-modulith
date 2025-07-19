package com.edumanager.user.api;

import com.edumanager.shared.dto.response.ApiResponse;
import com.edumanager.shared.dto.response.SuccessResponse;
import com.edumanager.user.application.dto.request.StudentSignupRequest;
import com.edumanager.user.application.dto.response.UserSignupResponse;
import com.edumanager.user.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/signup/student")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserSignupResponse> signupStudent(@Valid @RequestBody StudentSignupRequest request) {
        UserSignupResponse response = userService.registerStudentWithInviteCode(request);

        return SuccessResponse.created(response);
    }

    @GetMapping("/check-email")
    public ApiResponse<Boolean> checkEmailDuplicate(@RequestParam String email) {
        log.debug("이메일 중복 확인: email={}", email);

        boolean isExists = userService.isEmailExists(email);

        return SuccessResponse.of(!isExists);  // 사용 가능하면 true, 중복이면 false
    }
}
