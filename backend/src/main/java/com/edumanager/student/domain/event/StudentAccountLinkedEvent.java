package com.edumanager.student.domain.event;

import com.edumanager.shared.domain.enums.AccountStatus;
import com.edumanager.shared.events.DomainEvent;
import com.edumanager.shared.events.EventMetadata;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentAccountLinkedEvent(
        String eventId,
        LocalDateTime occurredOn,
        Long studentId,           // 연결된 학생 ID
        Long userId,              // 연결된 사용자 ID
        String studentCode,       // 학생 번호
        AccountStatus oldStatus,  // 이전 상태
        AccountStatus newStatus,  // 새로운 상태
        EventMetadata metadata
) implements DomainEvent {

    public static StudentAccountLinkedEvent of(Long studentId, Long userId, String studentCode,
                                               AccountStatus oldStatus, AccountStatus newStatus,
                                               Long linkedBy) {
        return new StudentAccountLinkedEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                studentId,
                userId,
                studentCode,
                oldStatus,
                newStatus,
                EventMetadata.of(linkedBy, "student-service")
        );
    }
}
