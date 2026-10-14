package com.hrmManagement.dto;

import com.hrmManagement.entity.enums.RefundStatus;
import jakarta.validation.constraints.NotNull;

public record RefundStatusChangeRequest(
        @NotNull(message = "Status is required")
        RefundStatus status
) {
}
