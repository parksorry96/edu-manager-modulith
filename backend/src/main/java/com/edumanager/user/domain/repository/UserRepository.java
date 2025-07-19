package com.edumanager.user.domain.repository;

import com.edumanager.shared.domain.enums.UserRole;
import com.edumanager.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 조회
     * @param email 이메일
     * @return 사용자 Optional
     */
    Optional<User> findByEmail(String email);

    /**
     * 활성화된 사용자만 이메일로 조회
     * @param email 이메일
     * @return 활성화된 사용자 Optional
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.enabled = true")
    Optional<User> findByEmailAndEnabledTrue(@Param("email") String email);

    /**
     * 전화번호로 사용자 조회
     * @param phone 전화번호
     * @return 사용자 Optional
     */
    Optional<User> findByPhone(String phone);

    /**
     * 이메일 중복 확인
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);

    /**
     * 전화번호 중복 확인 (null이 아닌 경우만)
     * @param phone 전화번호
     * @return 존재 여부
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.phone = :phone AND u.phone IS NOT NULL")
    boolean existsByPhoneAndPhoneIsNotNull(@Param("phone") String phone);

    /**
     * 초대코드로 사용자 조회
     * @param inviteCodeId 초대코드 ID
     * @return 사용자 Optional
     */
    Optional<User> findByInviteCodeId(Long inviteCodeId);

    /**
     * 학원별 활성 사용자 조회
     * @param academyId 학원 ID
     * @return 활성 사용자 리스트
     */
    @Query("SELECT u FROM User u WHERE u.academyId = :academyId AND u.enabled = true")
    List<User> findByAcademyIdAndEnabledTrue(@Param("academyId") Long academyId);

    /**
     * 역할별 사용자 조회
     * @param role 역할
     * @return 사용자 리스트
     */
    List<User> findByRole(UserRole role);

    /**
     * 학원별 역할별 활성 사용자 조회
     * @param academyId 학원 ID
     * @param role 역할
     * @return 사용자 리스트
     */
    @Query("SELECT u FROM User u WHERE u.academyId = :academyId AND u.role = :role AND u.enabled = true")
    List<User> findByAcademyIdAndRoleAndEnabledTrue(@Param("academyId") Long academyId,
                                                    @Param("role") UserRole role);

    /**
     * 이메일 인증되지 않은 사용자 조회
     * @param academyId 학원 ID
     * @return 미인증 사용자 리스트
     */
    @Query("SELECT u FROM User u WHERE u.academyId = :academyId AND u.emailVerified = false AND u.enabled = true")
    List<User> findUnverifiedEmailUsers(@Param("academyId") Long academyId);

    /**
     * 특정 기간 내 가입한 사용자 조회
     * @param academyId 학원 ID
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 사용자 리스트
     */
    @Query("SELECT u FROM User u WHERE u.academyId = :academyId " +
            "AND u.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY u.createdAt DESC")
    List<User> findByAcademyIdAndCreatedAtBetween(@Param("academyId") Long academyId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    /**
     * 소셜 로그인 사용자 조회
     * @param socialId 소셜 ID
     * @param socialProvider 소셜 제공자
     * @return 사용자 Optional
     */
    Optional<User> findBySocialIdAndSocialProvider(String socialId, String socialProvider);

    /**
     * 비활성화된 사용자 조회 (관리자용)
     * @param academyId 학원 ID
     * @return 비활성화된 사용자 리스트
     */
    @Query("SELECT u FROM User u WHERE u.academyId = :academyId AND u.enabled = false")
    List<User> findDisabledUsers(@Param("academyId") Long academyId);

    /**
     * 사용자명으로 검색 (부분 일치)
     * @param academyId 학원 ID
     * @param name 검색할 이름 (부분)
     * @return 검색된 사용자 리스트
     */
    @Query("SELECT u FROM User u WHERE u.academyId = :academyId " +
            "AND u.name LIKE %:name% AND u.enabled = true " +
            "ORDER BY u.name")
    List<User> findByAcademyIdAndNameContaining(@Param("academyId") Long academyId,
                                                @Param("name") String name);

    /**
     * 이메일 또는 이름으로 통합 검색
     * @param academyId 학원 ID
     * @param keyword 검색 키워드
     * @return 검색된 사용자 리스트
     */
    @Query("SELECT u FROM User u WHERE u.academyId = :academyId " +
            "AND (u.email LIKE %:keyword% OR u.name LIKE %:keyword%) " +
            "AND u.enabled = true " +
            "ORDER BY u.name")
    List<User> searchByEmailOrName(@Param("academyId") Long academyId,
                                   @Param("keyword") String keyword);

    /**
     * 최근 가입한 사용자 조회 (관리자 대시보드용)
     * @param academyId 학원 ID
     * @param limit 조회할 사용자 수
     * @return 최근 가입 사용자 리스트
     */
    @Query("SELECT u FROM User u WHERE u.academyId = :academyId AND u.enabled = true " +
            "ORDER BY u.createdAt DESC LIMIT :limit")
    List<User> findRecentUsers(@Param("academyId") Long academyId, @Param("limit") int limit);

    /**
     * 역할별 사용자 통계 조회
     * @param academyId 학원 ID
     * @return 역할별 사용자 수 통계
     */
    @Query("SELECT u.role, COUNT(u) FROM User u WHERE u.academyId = :academyId AND u.enabled = true GROUP BY u.role")
    List<Object[]> getUserStatsByRole(@Param("academyId") Long academyId);

    /**
     * 전화번호 인증 완료된 사용자 조회
     * @param academyId 학원 ID
     * @return 전화번호 인증 완료된 사용자 리스트
     */
    @Query("SELECT u FROM User u WHERE u.academyId = :academyId " +
            "AND u.phoneVerified = true AND u.enabled = true")
    List<User> findPhoneVerifiedUsers(@Param("academyId") Long academyId);

    /**
     * 완전히 인증된 사용자 조회 (이메일 + 전화번호)
     * @param academyId 학원 ID
     * @return 완전 인증된 사용자 리스트
     */
    @Query("SELECT u FROM User u WHERE u.academyId = :academyId " +
            "AND u.emailVerified = true AND u.phoneVerified = true AND u.enabled = true")
    List<User> findFullyVerifiedUsers(@Param("academyId") Long academyId);

    /**
     * 특정 초대코드로 가입한 사용자들 조회
     * @param inviteCodeId 초대코드 ID
     * @return 해당 초대코드로 가입한 사용자 리스트
     */
    List<User> findByInviteCodeIdOrderByCreatedAtDesc(Long inviteCodeId);
}
