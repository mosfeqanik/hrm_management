package com.sassmodel.mapper;

import com.sassmodel.dto.NotificationResponse;
import com.sassmodel.entity.Notification;

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
