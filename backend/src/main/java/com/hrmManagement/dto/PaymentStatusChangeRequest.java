package com.hrmManagement.dto;

import com.hrmManagement.entity.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public record PaymentStatusChangeRequest(
        @NotNull(message = "Status is required")
        PaymentStatus status
) {
}
