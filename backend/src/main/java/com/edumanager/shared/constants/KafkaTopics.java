package com.edumanager.shared.constants;

public final class KafkaTopics {
    // 도메인별 토픽 구조: {domain}.{event-type}
    public static final String USER_EVENTS = "user.events";           // 사용자 관련 모든 이벤트
    public static final String STUDENT_EVENTS = "student.events";     // 학생 관련 모든 이벤트
    public static final String PAYMENT_EVENTS = "payment.events";     // 결제 관련 이벤트
    public static final String NOTIFICATION_EVENTS = "notification.events"; // 알림 이벤트

    // 특정 이벤트 전용 토픽 (중요한 이벤트)
    public static final String USER_REGISTERED = "user.registered";   // 회원가입 전용
    public static final String STUDENT_LINKED = "student.linked";     // 학생 계정 연결 전용

    private KafkaTopics() {
        // 유틸리티 클래스 - 인스턴스 생성 방지
    }

}
