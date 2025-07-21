package com.edumanager.user.application.mapper;

import com.edumanager.shared.domain.enums.UserRole;
import com.edumanager.user.application.dto.request.StudentSignupRequest;
import com.edumanager.user.application.dto.response.UserSignupResponse;
import com.edumanager.user.domain.entity.User;
import com.edumanager.user.domain.enums.LoginType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface UserMapper {

    /**
     * User 엔티티를 UserSignupResponse DTO로 변환
     */
    @Mapping(target = "userId", source = "id")
    UserSignupResponse toSignupResponse(User user);

    /**
     * StudentSignupRequest DTO를 User 엔티티로 변환 (Builder 사용)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", constant = "STUDENT")
    @Mapping(target = "loginType", constant = "EMAIL")
    @Mapping(target = "inviteCodeId", ignore = true)
    @Mapping(target = "socialId", ignore = true)
    @Mapping(target = "socialProvider", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(StudentSignupRequest request);

    /**
     * 일반 회원가입을 위한 User 엔티티 생성
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "loginType", constant = "EMAIL")
    @Mapping(target = "inviteCodeId", ignore = true)
    @Mapping(target = "socialId", ignore = true)
    @Mapping(target = "socialProvider", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User createUser(String email, String name, String phone, UserRole role);
}
