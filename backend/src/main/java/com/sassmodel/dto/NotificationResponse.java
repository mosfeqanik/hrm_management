package com.sassmodel.dto;

import com.sassmodel.entity.enums.NotificationChannel;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long organizationId,
        Long userId,
        NotificationChannel channel,
        String subject,
        String body,
        LocalDateTime sentAt,
        boolean read
) {
}
