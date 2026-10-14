package com.sassmodel.dto;

import com.sassmodel.entity.enums.RefundStatus;
import jakarta.validation.constraints.NotNull;

public record RefundStatusChangeRequest(
        @NotNull(message = "Status is required")
        RefundStatus status
) {
}
