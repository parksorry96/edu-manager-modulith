package com.edumanager.shared.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("관리자"),
    TEACHER("강사"),
    PARENT("학부모"),
    STUDENT("학생");

    private final String description;


}
