package com.edumanager.application.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka                                    // Kafka 리스너 활성화
@Configuration                                  // Spring 설정 클래스
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")  // application.yml에서 부트스트랩 서버 주소 가져오기
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")  // 컨슈머 그룹 ID 가져오기
    private String groupId;

    // Producer 설정을 위한 Properties 생성
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // Kafka 브로커 주소 설정
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // 키 직렬화: 토픽 파티셔닝을 위한 키를 문자열로 직렬화
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // 값 직렬화: 이벤트 객체를 JSON으로 직렬화
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // 메시지 전송 안정성 설정 (모든 리플리카에서 확인 받기)
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");

        // 재시도 횟수 설정 (네트워크 오류 시 재전송)
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);

        // 배치 처리를 위한 대기 시간 (밀리초)
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 1);

        // 압축 타입 설정 (네트워크 효율성을 위해)
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // KafkaTemplate Bean 생성 (이벤트 발행을 위한 템플릿)
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // Consumer 설정을 위한 Properties 생성
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        // Kafka 브로커 주소 설정
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // 컨슈머 그룹 ID 설정 (같은 그룹의 컨슈머들은 메시지를 분산 처리)
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // 오프셋 재설정 정책 (earliest: 가장 오래된 메시지부터 읽기)
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // 자동 커밋 비활성화 (수동으로 처리 완료 확인)
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // 키 역직렬화
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // 값 역직렬화: JSON을 객체로 변환
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());

        // JSON 역직렬화 시 신뢰할 수 있는 패키지 설정 (보안)
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.edumanager.*");

        // 타입 정보 헤더 사용 (객체 타입 복원을 위해)
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, true);
        props.put(JsonDeserializer.TYPE_MAPPINGS,"userRegistered:com.edumanager.user.domain.event.UserRegisteredEvent,"+
                "studentLinked:com.edumanager.student.domain.event.StudentAccountLinkedEvent");
//        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.edumanager.shared.events.DomainEvent");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    // Kafka 리스너 컨테이너 팩토리 설정
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        // 위에서 설정한 컨슈머 팩토리 사용
        factory.setConsumerFactory(consumerFactory());

        // 동시 처리할 리스너 컨테이너 수 (CPU 코어 수에 맞게 조정)
        factory.setConcurrency(3);

        // 수동 커밋 모드 설정 (메시지 처리 완료 후 수동으로 오프셋 커밋)
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }
}
