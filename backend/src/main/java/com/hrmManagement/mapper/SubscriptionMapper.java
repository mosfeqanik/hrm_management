package com.hrmManagement.mapper;

import com.hrmManagement.dto.SubscriptionResponse;
import com.hrmManagement.entity.Subscription;

public final class SubscriptionMapper {

    private SubscriptionMapper() {
    }

    public static SubscriptionResponse toResponse(Subscription subscription) {
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getOrganization().getId(),
                subscription.getPlan().getId(),
                subscription.getPlan().getName(),
                subscription.getStatus(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getRenewalDate(),
                subscription.getTrialEndsAt(),
                subscription.isAutoRenew(),
                subscription.getCancelledAt()
        );
    }
}
