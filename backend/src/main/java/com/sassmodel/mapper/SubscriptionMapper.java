package com.sassmodel.mapper;

import com.sassmodel.dto.SubscriptionResponse;
import com.sassmodel.entity.Subscription;

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
