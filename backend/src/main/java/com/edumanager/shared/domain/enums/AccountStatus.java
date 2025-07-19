package com.edumanager.shared.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountStatus {
    UNLINKED("계정 미연결"),
    PENDING("연결 대기중"),
    LINKED("계정 연결됨"),
    REJECTED("연결 거부됨");

    private final String description;

    private boolean isLinked() {
        return this==LINKED;
    }

    public boolean isPending() {
        return this==PENDING;
    }

    public boolean canRequestLink(){
        return this==UNLINKED || this == REJECTED;
    }

    public boolean canUseApp(){
        return this==LINKED;
    }

    public boolean needsAdminAction() {
        return this == PENDING;
    }
}
