package com.edumanager.user.domain.service;


import com.edumanager.shared.domain.enums.AccountStatus;
import com.edumanager.shared.domain.enums.UserRole;
import com.edumanager.student.domain.entity.Student;
import com.edumanager.student.domain.repository.StudentRepository;
import com.edumanager.user.domain.entity.InviteCode;
import com.edumanager.user.domain.enums.InviteType;
import com.edumanager.user.domain.repository.InviteCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class InviteCodeService {

    private final InviteCodeRepository inviteCodeRepository;
    private final StudentRepository studentRepository;
    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int CODE_LENGTH = 8;

    public InviteCode generateGeneralInviteCode(Long academyId, Long createdBy, UserRole targetRole,
                                                int expiryDays, String description, int maxUses) {
        String code = generateUniqueCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(expiryDays);

        InviteCode inviteCode = InviteCode.builder()
                .code(code)
                .academyId(academyId)
                .createdBy(createdBy)
                .targetRole(targetRole)
                .expiresAt(expiresAt)
                .inviteType(InviteType.GENERAL)
                .targetStudentId(null)
                .description(description)
                .maxUses(maxUses)
                .build();

        InviteCode savedCode = inviteCodeRepository.save(inviteCode);

        log.info("초대코드 생성 완료 code: {}", savedCode.getCode());

        return savedCode;

    }

    public InviteCode generateStudentInviteCode(Long studentId, Long createdBy, UserRole targetRole, int expiryDays) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("학생을 찾을 수 없습니다." + studentId));

        String roleDescription =
                targetRole == UserRole.PARENT
                        ? "학부모"
                        : "학생";

        String autoDescription = String.format("%s %s용 초대코드", student.getName(), roleDescription);

        String code = generateUniqueCode();

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(expiryDays);

        InviteCode inviteCode = InviteCode.builder()
                .code(code)
                .academyId(student.getAcademyId())  // 학생의 학원 ID 사용
                .createdBy(createdBy)
                .targetRole(targetRole)
                .inviteType(InviteType.SPECIFIC_STUDENT)
                .targetStudentId(studentId)  // 🎯 핵심: 특정 학생 ID 연결
                .expiresAt(expiresAt)
                .description(autoDescription)
                .maxUses(1)  // 특정 학생용은 1회용으로 제한
                .build();

        InviteCode savedCode = inviteCodeRepository.save(inviteCode);

        log.info("특정 학생 초대코드 생성 완료 code : {}, 학생 : {}", savedCode.getCode(), student.getName());

        return savedCode;
    }

    public Student getTargetStudentByInviteCode(String code, UserRole userRole) {
        InviteCode inviteCode = inviteCodeRepository.findValidInviteCode(code, userRole, LocalDateTime.now())
                .orElseThrow(() ->
                        new IllegalArgumentException("유효하지 않은 초대코드입니다."));

        if (inviteCode.getInviteType() == InviteType.GENERAL || inviteCode.getTargetStudentId() == null) {
            log.info("일반 초대코드 사용: code={}", code);
            return null;
        }

        Student targetStudent = studentRepository.findById(inviteCode.getTargetStudentId())
                .orElseThrow(() ->
                        new IllegalStateException("연결된 학생을 찾을 수 없습니다."));

        log.info("특정 학생 초대코드 확인: code={}, studentName={}", code, targetStudent.getName());

        return targetStudent;


    }

    @Transactional
    public InviteCodeUsageResult validateAndUseCode(String code, UserRole userRole, Long userId) {
        log.info("초대코드 사용 및 학생 연결 처리: code={}, userRole={}, userId={}", code, userRole, userId);

        // 초대코드 검증 및 사용 처리
        InviteCode inviteCode = inviteCodeRepository.findValidInviteCode(code, userRole, LocalDateTime.now())
                .orElseThrow(() ->
                        new IllegalArgumentException("유효하지 않은 초대코드입니다."));

        inviteCode.use(userId);
        InviteCode usedCode = inviteCodeRepository.save(inviteCode);

        Student connectedStudent = null;

        // 특정 학생용 코드인 경우 학생 계정 연결 처리
        if (inviteCode.getInviteType() == InviteType.SPECIFIC_STUDENT && inviteCode.getTargetStudentId() != null) {

            connectedStudent = studentRepository.findById(inviteCode.getTargetStudentId())
                    .orElseThrow(() -> new IllegalStateException("연결할 학생을 찾을 수 없습니다."));

            //  핵심: 학생과 사용자 계정 자동 연결
            if (userRole == UserRole.PARENT) {
                // 학부모인 경우: Student는 그대로, User만 연결 정보 추가
                log.info("학부모 계정과 학생 연결: userId={}, studentId={}, studentName={}",
                        userId, connectedStudent.getId(), connectedStudent.getName());

            } else if (userRole == UserRole.STUDENT) {
                // 학생 본인인 경우: Student에 userId 연결
                // Student 엔티티에 userId 설정하는 메서드가 있다고 가정
                // connectedStudent.linkAccount(userId); // 이런 메서드가 Student에 있어야 함
                connectedStudent.linkUserAccount(userId);
                connectedStudent.updateAccountStatus(AccountStatus.LINKED);
                log.info("학생 본인 계정 연결: userId={}, studentId={}, studentName={}",
                        userId, connectedStudent.getId(), connectedStudent.getName());
            }

            studentRepository.save(connectedStudent);
        }

        log.info("초대코드 사용 완료: codeId={}, connectedStudentId={}",
                usedCode.getId(), connectedStudent != null ? connectedStudent.getId() : null);

        return new InviteCodeUsageResult(usedCode, connectedStudent);
    }

    private String generateUniqueCode() {
        String code;
        int attempts = 0;
        final int maxAttempts = 10;

        do {
            code = generateRandomCode();
            attempts++;

            if (attempts > maxAttempts) {
                throw new RuntimeException("초대코드 생성에 실패했습니다.");
            }
        } while (inviteCodeRepository.findByCode(code).isPresent());     //Optional 의 값 존재여부 판단 boolean 반환

        return code;
    }

    private String generateRandomCode() {
//        SecureRandom random = new SecureRandom(); // thread free이기 때문에 private static final 로 선언
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
        }

        return code.toString();
    }

    //    초대코드 사용 여부 내부 클래스
    public static class InviteCodeUsageResult {
        private final InviteCode usedInviteCode;
        private final Student connectedStudent;

        public InviteCodeUsageResult(InviteCode usedInviteCode, Student connectedStudent) {
            this.usedInviteCode = usedInviteCode;
            this.connectedStudent = connectedStudent;
        }

        public InviteCode getUsedInviteCode() {
            return usedInviteCode;
        }

        public Student getConnectedStudent() {
            return connectedStudent;
        }

        public boolean hasConnectedStudent() {
            return connectedStudent != null;
        }
    }
}
