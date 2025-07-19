package com.edumanager.shared.events;

import java.time.LocalDateTime;

public interface DomainEvent {
    String eventId();

    LocalDateTime occurredOn();
    default String eventType() {
        return this.getClass().getSimpleName();
    }

    default String version(){
        return "1.0";
    }
}
