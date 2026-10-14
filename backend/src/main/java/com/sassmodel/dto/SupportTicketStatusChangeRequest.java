package com.sassmodel.dto;

import com.sassmodel.entity.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record SupportTicketStatusChangeRequest(
        @NotNull(message = "Status is required")
        TicketStatus status
) {
}
