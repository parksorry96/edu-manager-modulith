package com.edumanager.user.domain.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginType {

    EMAIL("이메일"),
    PHONE("휴대폰"),
    KAKAO("카카오"),
    NAVER("네이버"),
    GOOGLE("구글");

    private final String description;
}
