package com.reindecar.dto.user;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    long expiresIn,
    UserResponse user
) {
}
