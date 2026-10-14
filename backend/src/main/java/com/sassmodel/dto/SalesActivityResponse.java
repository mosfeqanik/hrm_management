package com.sassmodel.dto;

import com.sassmodel.entity.enums.SalesActivityType;

import java.time.LocalDateTime;

public record SalesActivityResponse(
        Long id,
        Long leadId,
        Long performedByUserId,
        SalesActivityType type,
        String note,
        LocalDateTime scheduledAt,
        LocalDateTime completedAt
) {
}
