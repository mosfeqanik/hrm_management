package com.hrmManagement.dto;

import com.hrmManagement.entity.enums.BillingCycle;

import java.math.BigDecimal;

public record SubscriptionPlanResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String currency,
        BillingCycle billingCycle,
        Integer maxUsers,
        String features,
        int trialDays,
        boolean active
) {
}
