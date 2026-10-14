package com.hrmManagement.dto;

import com.hrmManagement.entity.enums.*;

import java.math.BigDecimal;
import java.time.*;

public record ShiftResponse(
        Long id,
        Long organizationId,
        String name,
        LocalTime startTime,
        LocalTime endTime,
        boolean active
) {
}
