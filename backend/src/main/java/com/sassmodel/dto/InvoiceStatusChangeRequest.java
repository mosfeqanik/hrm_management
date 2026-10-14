package com.sassmodel.dto;

import com.sassmodel.entity.enums.InvoiceStatus;
import jakarta.validation.constraints.NotNull;

public record InvoiceStatusChangeRequest(
        @NotNull(message = "Status is required")
        InvoiceStatus status
) {
}
