package com.edumanager.shared.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LinkRequestStatus {
    PENDING("승인 대기"),
    APPROVED("승인 완료"),
    REJECTED("거부됨"),
    CANCELLED("취소됨");

    private final String description;


}
