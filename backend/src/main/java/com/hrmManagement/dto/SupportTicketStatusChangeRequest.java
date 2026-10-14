package com.hrmManagement.dto;

import com.hrmManagement.entity.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record SupportTicketStatusChangeRequest(
        @NotNull(message = "Status is required")
        TicketStatus status
) {
}
