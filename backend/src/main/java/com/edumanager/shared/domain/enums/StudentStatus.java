package com.edumanager.shared.domain.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudentStatus {
    ENROLLED("재원"),
    WITHDRAWN("퇴원");

    private final String description;
}
