package com.hrmManagement.mapper;

import com.hrmManagement.dto.NotificationResponse;
import com.hrmManagement.entity.Notification;

public final class NotificationMapper {

    private NotificationMapper() {
    }

    public static NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getOrganization().getId(),
                notification.getUser().getId(),
                notification.getChannel(),
                notification.getSubject(),
                notification.getBody(),
                notification.getSentAt(),
                notification.isRead()
        );
    }
}
