package com.sassmodel.dto;

import com.sassmodel.entity.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public record PaymentStatusChangeRequest(
        @NotNull(message = "Status is required")
        PaymentStatus status
) {
}
