package com.sassmodel.dto;

import com.sassmodel.entity.enums.BillingCycle;

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
