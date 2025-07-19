package com.edumanager.user.application.service;

import com.edumanager.shared.exception.BusinessException;
import com.edumanager.shared.exception.ErrorCode;
import com.edumanager.shared.security.JwtTokenProvider;
import com.edumanager.user.application.dto.request.LoginRequest;
import com.edumanager.user.application.dto.response.LoginResponse;
import com.edumanager.user.application.dto.response.UserSignupResponse;
import com.edumanager.user.application.mapper.UserMapper;
import com.edumanager.user.domain.entity.User;
import com.edumanager.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.security.auth.Login;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    public LoginResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(()-> new BusinessException(ErrorCode.AUTHENTICATION_FAILED));

        if(!user.isEnabled()){
            throw new BusinessException(ErrorCode.USER_ACCOUNT_DISABLED);
        }

        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(),user.getEmail(),user.getRole());

        long expiresIn = jwtTokenProvider.getAccessTokenExpiration();

        UserSignupResponse userResponse = userMapper.toSignupResponse(user);

        return LoginResponse.of(accessToken,expiresIn,userResponse);
    }

    public void logout(String authorization){
        //redis를 활용하여 토큰 블랙리스트 처리 예정

    }

}
