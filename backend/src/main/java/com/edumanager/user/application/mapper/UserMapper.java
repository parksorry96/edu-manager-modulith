package com.edumanager.user.application.mapper;

import com.edumanager.shared.domain.enums.UserRole;
import com.edumanager.user.application.dto.request.StudentSignupRequest;
import com.edumanager.user.application.dto.response.UserSignupResponse;
import com.edumanager.user.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",  // Spring Bean으로 등록
        unmappedTargetPolicy = ReportingPolicy.IGNORE,  // 매핑되지 않은 필드 무시
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS  // null 체크 전략
)
public interface UserMapper {

    /**
     * User 엔티티를 UserSignupResponse DTO로 변환
     */
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    UserSignupResponse toSignupResponse(User user);

    /**
     * StudentSignupRequest DTO를 User 엔티티로 변환
     */
    @Mapping(target = "id", ignore = true)  // ID는 DB에서 자동 생성
    @Mapping(target = "password", ignore = true)  // 비밀번호는 암호화 후 별도 설정
    @Mapping(target = "role", constant = "STUDENT")  // 학생 역할 고정
    @Mapping(target = "academyId", constant = "1L")  // 현재는 고정값
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "phoneVerified", constant = "false")
    @Mapping(target = "inviteCodeId", ignore = true)  // 별도 설정
    @Mapping(target = "socialId", ignore = true)
    @Mapping(target = "socialProvider", ignore = true)
    @Mapping(target = "loginType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(StudentSignupRequest request);

    /**
     * 일반 회원가입을 위한 User 엔티티 생성
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)  // 암호화 후 별도 설정
    @Mapping(target = "academyId", constant = "1L")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "phoneVerified", constant = "false")
    @Mapping(target = "inviteCodeId", ignore = true)
    @Mapping(target = "socialId", ignore = true)
    @Mapping(target = "socialProvider", ignore = true)
    @Mapping(target = "loginType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User createUser(String email, String name, String phone, UserRole role);
}
