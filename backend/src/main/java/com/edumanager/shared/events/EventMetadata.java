package com.edumanager.shared.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventMetadata(
        Long userId,                    // 발생시킨 사용자의 ID
        String sourceService,           // 이벤트 발생 모듈, 서비스
        String correlationId,           // 상관관계 ID
        String additionalContext,       // 추가 컨텍스트(JSON)
        LocalDateTime createAt          // 생성시간
) {

    // 기본 matadata 생성
    public static EventMetadata of(Long userId, String sourceService) {
        return new EventMetadata(
                userId,
                sourceService,
                UUID.randomUUID().toString(),
                null,
                LocalDateTime.now()
        );
    }

    public static EventMetadata of(Long userId, String sourceService, String correlationId) {
        return new EventMetadata(
                userId,
                sourceService,
                correlationId,
                null,
                LocalDateTime.now()
        );

    }


    // 유효성 검증
    public EventMetadata{
        if(sourceService == null){
            throw new NullPointerException("sourceService는 필수입니다");
        }
        if(correlationId == null){
            correlationId = UUID.randomUUID().toString();
        }
        if(createAt == null){
            createAt = LocalDateTime.now();
        }
    }

}
