package com.edumanager.user.domain.entity;

import com.edumanager.shared.domain.BaseTimeEntity;
import com.edumanager.user.domain.enums.LoginType;
import com.edumanager.shared.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Getter             //무분별한 엔티티 수정 방지 setter없이 getter만 사용
@NoArgsConstructor(access = AccessLevel.PROTECTED)       //외부에서 new User 로 entity 생성 방지
@AllArgsConstructor
@SuperBuilder
public class User extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(unique = true)
    private String phone;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;


    @Column(nullable = false)
    private boolean enabled = true;


    @Column(nullable = false)
    private boolean emailVerified = false;


    @Column(nullable = false)
    private boolean phoneVerified = false;

    @Column
    private String socialId;        // 카카오/네이버 고유 ID

    @Column
    private String socialProvider;  // KAKAO, NAVER 등

    @Column(name="academy_id", nullable = false)
    private Long academyId = 1L;        //추후 서비스 확장 대비(현재 고정값)

    @Column
    private Long inviteCodeId;

    public void setInviteCode(Long inviteCodeId) {
        this.inviteCodeId = inviteCodeId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_"+role.name())
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // 계정 비활성화 메소드
    public void disable() {
        this.enabled = false;
    }

    public void enable() {
        this.enabled = true;
    }

    //비밀번호 변경 메소드
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }



    /**권한 확인 메소드 **/
    public boolean hasRole(UserRole targetRole){
        return this.role == targetRole;
    }

    public boolean isAdmin(){
        return hasRole(UserRole.ADMIN);
    }

    public boolean isTeacher(){
        return hasRole(UserRole.TEACHER);
    }

    public boolean isStudent(){
        return hasRole(UserRole.STUDENT);
    }

    public boolean isParent(){
        return hasRole(UserRole.PARENT);
    }

    public void verifyPhone(){
        this.phoneVerified = true;
    }

    public void verifyEmail(){
        this.emailVerified = true;
    }

    public boolean isFullyVerified() {
        return emailVerified && phoneVerified;
    }

    public void setSocialInfo(String socialId, String provider){
        this.socialId = socialId;
        this.socialProvider = provider;
        this.loginType = LoginType.valueOf(provider.toUpperCase());
    }

    public void updateBasicInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }

}
