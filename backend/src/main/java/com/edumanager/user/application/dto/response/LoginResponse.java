package com.edumanager.user.application.dto.response;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserSignupResponse user
) {
    public static LoginResponse of(String accessToken, long expiresIn, UserSignupResponse user) {
        return new LoginResponse(accessToken, "Bearer", expiresIn, user);
    }
}
