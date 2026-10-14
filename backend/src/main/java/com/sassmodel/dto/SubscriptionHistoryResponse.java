package com.sassmodel.dto;

import com.sassmodel.entity.enums.SubscriptionStatus;

import java.time.LocalDateTime;

public record SubscriptionHistoryResponse(
        Long id,
        Long subscriptionId,
        Long fromPlanId,
        String fromPlanName,
        Long toPlanId,
        String toPlanName,
        SubscriptionStatus fromStatus,
        SubscriptionStatus toStatus,
        Long changedByUserId,
        String changedByName,
        String note,
        LocalDateTime changedAt
) {
}
