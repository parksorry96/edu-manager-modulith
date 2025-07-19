package com.edumanager.application.config;

import com.edumanager.shared.constants.KafkaTopics;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // Kafka Admin 클라이언트 설정 (토픽 관리용)
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    // 사용자 이벤트 토픽 (파티션 3개, 복제본 1개)
    @Bean
    public NewTopic userEventsTopic() {
        return new NewTopic(KafkaTopics.USER_EVENTS, 3, (short) 1);
    }

    // 학생 이벤트 토픽
    @Bean
    public NewTopic studentEventsTopic() {
        return new NewTopic(KafkaTopics.STUDENT_EVENTS, 3, (short) 1);
    }

    // 사용자 등록 전용 토픽 (중요 이벤트이므로 파티션 더 많이)
    @Bean
    public NewTopic userRegisteredTopic() {
        return new NewTopic(KafkaTopics.USER_REGISTERED, 6, (short) 1);
    }

    // 학생 계정 연결 토픽
    @Bean
    public NewTopic studentLinkedTopic() {
        return new NewTopic(KafkaTopics.STUDENT_LINKED, 3, (short) 1);
    }

    // 알림 이벤트 토픽
    @Bean
    public NewTopic notificationEventsTopic() {
        return new NewTopic(KafkaTopics.NOTIFICATION_EVENTS, 6, (short) 1);
    }
}
