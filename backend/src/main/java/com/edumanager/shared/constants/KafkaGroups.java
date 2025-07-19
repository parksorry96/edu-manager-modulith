package com.edumanager.shared.constants;

public class KafkaGroups {
    // 기본 그룹 (모든 이벤트 처리)
    public static final String EDU_MANAGER_GROUP = "edu-manager-group";

    // 도메인별 전용 그룹
    public static final String STUDENT_SERVICE_GROUP = "student-service-group";      // 학생 서비스 전용
    public static final String NOTIFICATION_SERVICE_GROUP = "notification-service-group"; // 알림 서비스

    public static final String ANALYTICS_SERVICE_GROUP = "analytics-service-group";  // 분석 서비스 전용

    // 특수 목적 그룹
    public static final String AUDIT_GROUP = "audit-group";           // 감사/로깅용
    public static final String RETRY_GROUP = "retry-group";           // 재시도 처리용

    private KafkaGroups() {
        // 유틸리티 클래스 - 인스턴스 생성 방지
    }
}
