package com.edumanager.user.application.service;

import com.edumanager.shared.domain.enums.UserRole;
import com.edumanager.shared.exception.BusinessException;
import com.edumanager.shared.exception.ErrorCode;
import com.edumanager.shared.infrastructure.messaging.EventPublisher;
import com.edumanager.user.application.dto.request.StudentSignupRequest;
import com.edumanager.user.application.dto.response.UserSignupResponse;
import com.edumanager.user.application.mapper.UserMapper;
import com.edumanager.user.domain.entity.User;
import com.edumanager.user.domain.event.UserRegisteredEvent;
import com.edumanager.user.domain.repository.UserRepository;
import com.edumanager.user.domain.service.InviteCodeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final InviteCodeService inviteCodeService;
    private final PasswordEncoder passwordEncoder;
    private final EventPublisher eventPublisher;
    private final UserMapper userMapper;

    public UserSignupResponse registerStudentWithInviteCode(StudentSignupRequest request){
        validateRegistrationData(request.email(),request.password(),request.name());

        Long targetStudentId = inviteCodeService.getTargetStudentByInviteCode(
                request.inviteCode(),
                UserRole.STUDENT
        );

        if(targetStudentId !=null){
            log.info("특정 학생용 초대코드 학생 : {}", targetStudentId);
        }else{
            log.info("일반 초대코드");
        }
        User user = User.builder()
                .email(request.email())                    // 직접 request에서 가져오기
                .password(passwordEncoder.encode(request.password())) // 직접 request에서 가져오기
                .name(request.name())
                .phone(request.phone())
                .role(UserRole.STUDENT)
                .academyId(1L)
                .enabled(true)
                .emailVerified(false)
                .phoneVerified(false)
                .build();

        User savedUser = userRepository.save(user);

        // 초대코드 사용처리 및 학생 계정 연결
        InviteCodeService.InviteCodeUsageResult usageResult=
                inviteCodeService.validateAndUseCode(request.inviteCode(), UserRole.STUDENT, savedUser.getId());

        UserRegisteredEvent event = UserRegisteredEvent.of(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getRole(),
                savedUser.getAcademyId(),
                request.inviteCode(),
                targetStudentId
        );
        eventPublisher.publish(event);

        return userMapper.toSignupResponse(savedUser);

    }

    /**
     * 일반 회원가입 (관리자/강사용)
     */
    public UserSignupResponse registerUser(String email, String password, String name,
                                           String phone, UserRole role) {
        log.info("일반 회원가입 시작: email={}, role={}", email, role);

        // 1. 기본 유효성 검증
        validateRegistrationData(email, password, name);

        // 2. 학생 역할은 초대코드 필수
        if (role == UserRole.STUDENT) {
            throw new BusinessException(ErrorCode.STUDENT_INVITE_CODE_REQUIRED);
        }

        // 3. 사용자 생성
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .phone(phone)
                .role(role)
                .academyId(1L)
                .enabled(true)
                .emailVerified(false)
                .phoneVerified(false)
                .build();

        // 4. 사용자 저장
        User savedUser = userRepository.save(user);

        // 5. 이벤트 발행
        UserRegisteredEvent event = UserRegisteredEvent.of(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getRole(),
                savedUser.getAcademyId(),
                null,
                null
        );
        eventPublisher.publish(event);

        log.info("일반 회원가입 완료: userId={}, role={}", savedUser.getId(), savedUser.getRole());

        // 6. MapStruct로 응답 DTO 변환
        return userMapper.toSignupResponse(savedUser);
    }

    /**
     * 기본 유효성 검증
     */
    private void validateRegistrationData(String email, String password, String name) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
        }

        // 비밀번호 강도 검증 (최소 8자, 영문+숫자 조합)
        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.USER_PASSWORD_TOO_SHORT);
        }

        // 이름 유효성 검증
        if (name == null || name.trim().length() < 2) {
            throw new BusinessException(ErrorCode.USER_NAME_INVALID);
        }
    }

    /**
     * 이메일로 사용자 조회
     */
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 이메일 중복 확인
     */
    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

}
