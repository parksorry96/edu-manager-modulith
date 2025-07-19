package com.edumanager.shared.infrastructure.messaging;

import com.edumanager.shared.constants.KafkaTopics;
import com.edumanager.shared.events.DomainEvent;
import com.edumanager.student.domain.event.StudentAccountLinkedEvent;
import com.edumanager.user.domain.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 이벤트를 적절한 토픽으로 발행
     * @param event 발행할 도메인 이벤트
     */
    public void publish(DomainEvent event) {
        String topic = determineTopicByEvent(event);
        String key = generateEventKey(event);

        log.info("이벤트 발행 시작: {} -> 토픽: {}, 키: {}",
                event.eventType(), topic, key);

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, exception) -> {
            if (exception == null) {
                log.info("이벤트 발행 성공: {} (오프셋: {})",
                        event.eventType(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("이벤트 발행 실패: {} - {}",
                        event.eventType(),
                        exception.getMessage(), exception);
            }
        });
    }

    /**
     * 이벤트 타입에 따라 적절한 토픽 결정
     */
    private String determineTopicByEvent(DomainEvent event) {
        return switch (event.eventType()) {
            case "UserRegisteredEvent" -> KafkaTopics.USER_REGISTERED;
            case "StudentAccountLinkedEvent" -> KafkaTopics.STUDENT_LINKED;
            case "UserUpdatedEvent", "UserDeactivatedEvent" -> KafkaTopics.USER_EVENTS;
            case "StudentCreatedEvent", "StudentUpdatedEvent" -> KafkaTopics.STUDENT_EVENTS;
            default -> {
                // 도메인 패키지명으로 토픽 결정
                String packageName = event.getClass().getPackageName();
                if (packageName.contains(".user.")) {
                    yield KafkaTopics.USER_EVENTS;
                } else if (packageName.contains(".student.")) {
                    yield KafkaTopics.STUDENT_EVENTS;
                } else {
                    yield "general.events"; // 기본 토픽
                }
            }
        };
    }

    /**
     * 이벤트의 파티션 키 생성 (같은 엔티티의 이벤트는 같은 파티션으로)
     */
    private String generateEventKey(DomainEvent event) {
        // Record 패턴 매칭으로 키 추출 (Java 17+)
        return switch (event) {
            case UserRegisteredEvent userEvent -> "user:" + userEvent.userId();
            case StudentAccountLinkedEvent studentEvent -> "student:" + studentEvent.studentId();
            default -> event.eventId(); // 기본적으로 이벤트 ID 사용
        };
    }
}
