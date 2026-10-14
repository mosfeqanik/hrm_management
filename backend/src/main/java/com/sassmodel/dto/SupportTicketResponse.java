package com.sassmodel.dto;

import com.sassmodel.entity.enums.TicketPriority;
import com.sassmodel.entity.enums.TicketStatus;
import com.sassmodel.entity.enums.TicketType;

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
