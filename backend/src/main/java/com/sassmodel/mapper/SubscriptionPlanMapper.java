package com.sassmodel.mapper;

import com.sassmodel.dto.SubscriptionPlanResponse;
import com.sassmodel.entity.SubscriptionPlan;

public final class SubscriptionPlanMapper {

    private SubscriptionPlanMapper() {
    }

    public static SubscriptionPlanResponse toResponse(SubscriptionPlan plan) {
        return new SubscriptionPlanResponse(
                plan.getId(),
                plan.getName(),
                plan.getDescription(),
                plan.getPrice(),
                plan.getCurrency(),
                plan.getBillingCycle(),
                plan.getMaxUsers(),
                plan.getFeatures(),
                plan.getTrialDays(),
                plan.isActive()
        );
    }
}
