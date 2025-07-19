package com.edumanager.user.domain.repository;

import com.edumanager.user.domain.entity.InviteCode;
import com.edumanager.user.domain.enums.InviteType;
import com.edumanager.shared.domain.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InviteCodeRepository extends JpaRepository<InviteCode, Long> {

    Optional<InviteCode> findByCode(String code);

    @Query("SELECT ic FROM InviteCode ic WHERE ic.expiresAt < :now AND ic.isUsed = false")
    List<InviteCode> findExpiredCodes(@Param("now") LocalDateTime now);

    /**
     * 특정 학생의 초대코드 목록 조회 (최신순)
     */
    List<InviteCode> findByTargetStudentIdOrderByCreatedAtDesc(Long targetStudentId);

    /**
     * 특정 학생의 활성 초대코드 개수 조회 (중복 생성 방지용)
     */
    @Query("SELECT COUNT(ic) FROM InviteCode ic WHERE ic.targetStudentId = :studentId " +
            "AND ic.targetRole = :targetRole " +
            "AND ic.isUsed = false " +
            "AND ic.expiresAt > :now")
    long countActiveCodesForStudent(@Param("studentId") Long studentId,
                                    @Param("targetRole") UserRole targetRole,
                                    @Param("now") LocalDateTime now);

    /**
     * 초대 타입별 활성 코드 조회
     */
    @Query("SELECT ic FROM InviteCode ic WHERE ic.academyId = :academyId " +
            "AND ic.inviteType = :inviteType " +
            "AND ic.isUsed = false " +
            "AND ic.expiresAt > :now " +
            "ORDER BY ic.createdAt DESC")
    List<InviteCode> findActiveCodesByType(@Param("academyId") Long academyId,
                                           @Param("inviteType") InviteType inviteType,
                                           @Param("now") LocalDateTime now);

    /**
     * 생성자별 초대코드 조회 (이미 있는 것 같지만 확인)
     */
    List<InviteCode> findByCreatedByOrderByCreatedAtDesc(Long createdBy);

    /**
     * 특정 사용자가 사용한 초대코드 조회
     */
    Optional<InviteCode> findByUsedBy(Long userId);

    /**
     * 학원별 초대 타입과 역할로 필터링된 목록
     */
    @Query("SELECT ic FROM InviteCode ic WHERE ic.academyId = :academyId " +
            "AND ic.inviteType = :inviteType " +
            "AND ic.targetRole = :targetRole " +
            "ORDER BY ic.createdAt DESC")
    List<InviteCode> findByAcademyAndTypeAndRole(@Param("academyId") Long academyId,
                                                 @Param("inviteType") InviteType inviteType,
                                                 @Param("targetRole") UserRole targetRole);

    @Query("SELECT ic FROM InviteCode ic WHERE ic.code = :code " +
            "AND ic.targetRole = :targetRole " +
            "AND ic.isUsed = false " +
            "AND ic.currentUses < ic.maxUses " +
            "AND ic.expiresAt > :now")
    Optional<InviteCode> findValidInviteCode(@Param("code") String code,
                                             @Param("targetRole") UserRole targetRole,
                                             @Param("now") LocalDateTime now);

    List<InviteCode> findByAcademyIdOrderByCreatedAtDesc(Long academyId);

    @Query("SELECT ic FROM InviteCode ic WHERE ic.academyId = :academyId " +
            "AND ic.isUsed = false " +
            "AND ic.currentUses < ic.maxUses " +
            "AND ic.expiresAt > :now " +
            "ORDER BY ic.createdAt DESC")
    List<InviteCode> findActiveCodesByAcademy(@Param("academyId") Long academyId,
                                              @Param("now") LocalDateTime now);
}
