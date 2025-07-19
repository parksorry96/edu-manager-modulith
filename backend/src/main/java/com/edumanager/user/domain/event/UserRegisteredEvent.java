package com.edumanager.user.domain.event;

import com.edumanager.shared.domain.enums.UserRole;
import com.edumanager.shared.events.DomainEvent;
import com.edumanager.shared.events.EventMetadata;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserRegisteredEvent(
        String eventId,
        LocalDateTime occurredOn,
        Long userId,
        String email,
        String name,
        UserRole role,
        Long academyId,
        EventMetadata metadata

) implements DomainEvent {
    public static UserRegisteredEvent of(Long userId, String email, String name, UserRole role, Long academyId, Long createdBy) {
        return new UserRegisteredEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                userId,
                email,
                name,
                role,
                academyId,
                EventMetadata.of(createdBy, "user-service")
        );
    }

    public UserRegisteredEvent {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("userId는 양수여야 합니다");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email은 필수입니다");
        }
        if (role == null) {
            throw new IllegalArgumentException("role은 필수입니다");
        }
        if (eventId == null) {
            eventId = UUID.randomUUID().toString();
        }
        if (occurredOn == null) {
            occurredOn = LocalDateTime.now();
        }
    }

}
