package com.hrmManagement.mapper;

import com.hrmManagement.dto.SubscriptionPlanResponse;
import com.hrmManagement.entity.SubscriptionPlan;

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
