package com.sassmodel.dto;

import com.sassmodel.entity.enums.TicketPriority;
import com.sassmodel.entity.enums.TicketType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SupportTicketRequest(

        Long organizationId,

        @NotBlank(message = "Subject is required")
        @Size(max = 200, message = "Subject must be at most 200 characters")
        String subject,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Type is required")
        TicketType type,

        @NotNull(message = "Priority is required")
        TicketPriority priority
) {
}
