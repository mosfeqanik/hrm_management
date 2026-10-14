package com.sassmodel.mapper;

import com.sassmodel.dto.SalesActivityResponse;
import com.sassmodel.entity.SalesActivity;

public final class SalesActivityMapper {

    private SalesActivityMapper() {
    }

    public static SalesActivityResponse toResponse(SalesActivity activity) {
        return new SalesActivityResponse(
                activity.getId(),
                activity.getLead().getId(),
                activity.getPerformedBy() != null ? activity.getPerformedBy().getId() : null,
                activity.getType(),
                activity.getNote(),
                activity.getScheduledAt(),
                activity.getCompletedAt()
        );
    }
}
