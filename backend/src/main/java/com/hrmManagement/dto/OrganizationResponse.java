package com.hrmManagement.dto;

import com.hrmManagement.entity.enums.OrganizationStatus;

import java.time.LocalDateTime;

public record OrganizationResponse(
        Long id,
        String name,
        String slug,
        OrganizationStatus status,
        String email,
        String phone,
        String address,
        LocalDateTime trialEndsAt,
        LocalDateTime activatedAt,
        Long ownerUserId
) {
}
