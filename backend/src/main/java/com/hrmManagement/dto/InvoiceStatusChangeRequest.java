package com.hrmManagement.dto;

import com.hrmManagement.entity.enums.InvoiceStatus;
import jakarta.validation.constraints.NotNull;

public record InvoiceStatusChangeRequest(
        @NotNull(message = "Status is required")
        InvoiceStatus status
) {
}
