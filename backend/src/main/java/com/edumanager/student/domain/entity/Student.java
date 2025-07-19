package com.edumanager.student.domain.entity;

import com.edumanager.shared.domain.BaseTimeEntity;
import com.edumanager.shared.domain.enums.AccountStatus;
import com.edumanager.shared.domain.enums.Gender;
import com.edumanager.shared.domain.enums.StudentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Entity
@Table(name = "students", indexes = {
        @Index(name = "idx_student_user_id", columnList = "user_id"),              // User 조회 최적화
        @Index(name = "idx_student_code", columnList = "student_code"),            // 학생번호 조회 최적화
        @Index(name = "idx_student_academy", columnList = "academy_id"),           // 학원별 조회 최적화
        @Index(name = "idx_student_status", columnList = "status"),                // 재원상태별 조회 최적화
        @Index(name = "idx_student_account_status", columnList = "account_status"), // 계정연결상태별 조회 최적화
        @Index(name = "idx_student_matching", columnList = "name, birth_date"),    // 학생 매칭용 복합 인덱스
        @Index(name = "idx_student_parent_phone", columnList = "parent_phone") // 학부모 연락처 검색 최적화
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Slf4j
public class Student extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // 계정이 없더라도 기존 학생부 데이터 등록가능하게 not null 로 테이블 생성
    @Column(name = "user_id", unique = true)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false, unique = true, length = 30)
    private String studentCode;

    @Column(length = 40)
    private String school;

    @Column
    private Integer grade;

    @Column(length = 20)
    private String className;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StudentStatus status;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "withdrawal_date")
    private LocalDate withdrawalDate;

    @Column(name = "withdrawal_reason", length = 200)
    private String withdrawalReason;


    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false, length = 20)
    private AccountStatus accountStatus = AccountStatus.UNLINKED;

    private String parentPhone;

    private String parentName;

    @Column(length = 500)
    private String notes;

    @Column(name = "birth_date")
    private LocalDate birthDate;


    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    private Gender gender;

    @Column(name = "academy_id", nullable = false)
    private Long academyId = 1L;

    public void updateBasicInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }


    public void updateSchoolInfo(String school, Integer grade, String className) {
        this.school = school;
        this.grade = grade;
        this.className = className;
    }

    public void updatePrimaryParent(String parentName, String parentPhone) {
        this.parentName = parentName;
        this.parentPhone = parentPhone;
    }

    public void changeStatus(StudentStatus newStatus) {
        this.status = newStatus;

        if (newStatus == StudentStatus.WITHDRAWN) {
            this.withdrawalDate = LocalDate.now();
        } else {
            this.withdrawalDate = null;
            this.withdrawalReason = null;
        }
    }

    public void withdraw(String withdrawalReason) {
        this.status = StudentStatus.WITHDRAWN;
        this.withdrawalReason = withdrawalReason;
        this.withdrawalDate = LocalDate.now();
    }

    public void reenroll() {
        this.status = StudentStatus.ENROLLED;
        this.withdrawalDate = null;
        this.withdrawalReason = null;
    }

    public boolean isActive() {
        return status == StudentStatus.ENROLLED;
    }

    public boolean isWithdrawn() {
        return status == StudentStatus.WITHDRAWN;
    }

    public boolean hasLinkedAccount() {
        return accountStatus == AccountStatus.LINKED && userId != null;
    }

    public boolean isPendingAccountLink() {
        return accountStatus == AccountStatus.PENDING;
    }

    public boolean canRequestAccountLink() {
        return accountStatus == AccountStatus.UNLINKED || accountStatus == AccountStatus.REJECTED;
    }

    public boolean canUseApp() {
        return hasLinkedAccount() && isActive();
    }

    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        return LocalDate.now().getYear() - birthDate.getYear();
    }

    public long getEnrollmentMonths() {
        if (enrollmentDate == null) {
            return 0;
        }

        LocalDate endDate = isWithdrawn() ? withdrawalDate : LocalDate.now();
        return java.time.Period.between(enrollmentDate, endDate).toTotalMonths();
    }

    public String getSchoolInfo() {
        if (school == null) {
            return "학교 정보 없음";
        }
        return grade != null ? String.format("%s %d학년", school, grade) : school;
    }

    public void linkUserAccount(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("ID를 입력해 주세요");
        }

        if (this.userId != null && !userId.equals(this.userId)) {
            throw new IllegalStateException("이미 다른 계정과 연결되어 있습니다.");
        }

        this.userId = userId;
        this.accountStatus = AccountStatus.LINKED;
        log.info("계정 연결 완료");
    }

    public void updateAccountStatus(AccountStatus newAccountStatus) {
        if (newAccountStatus == null) {
            throw new IllegalArgumentException("상태는 null일 수 없습니다.");
        }
        this.accountStatus = newAccountStatus;

        if (newAccountStatus != AccountStatus.LINKED) {
            this.userId = null;
        }
        log.info("계정상태변경");
    }

    public String getAccountStatusDisplay() {
        return switch (this.accountStatus) {
            case UNLINKED -> "계정 미연결";
            case PENDING -> "연결 승인 대기";
            case LINKED -> "계정 연결됨";
            case REJECTED -> "연결 거부됨";
            default -> "알 수 없음";
        };
    }
}