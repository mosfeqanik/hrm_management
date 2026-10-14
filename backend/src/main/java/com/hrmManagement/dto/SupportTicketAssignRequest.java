package com.hrmManagement.dto;

import jakarta.validation.constraints.NotNull;

public record SupportTicketAssignRequest(
        @NotNull(message = "assignedToUserId is required")
        Long assignedToUserId
) {
}
