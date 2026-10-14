package com.hrmManagement.dto;

import com.hrmManagement.entity.enums.TicketPriority;
import com.hrmManagement.entity.enums.TicketStatus;
import com.hrmManagement.entity.enums.TicketType;

import java.time.LocalDateTime;

public record SupportTicketResponse(
        Long id,
        Long organizationId,
        Long createdByUserId,
        String createdByName,
        Long assignedToUserId,
        String assignedToName,
        String ticketNumber,
        String subject,
        String description,
        TicketType type,
        TicketStatus status,
        TicketPriority priority,
        LocalDateTime resolvedAt,
        LocalDateTime closedAt
) {
}
