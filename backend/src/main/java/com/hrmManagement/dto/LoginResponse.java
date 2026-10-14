package com.hrmManagement.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        String fullName,
        String userType
) {
}
