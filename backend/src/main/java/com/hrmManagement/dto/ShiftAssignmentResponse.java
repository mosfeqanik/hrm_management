package com.hrmManagement.dto;

import com.hrmManagement.entity.enums.*;

import java.math.BigDecimal;
import java.time.*;

public record ShiftAssignmentResponse(
        Long id,
        Long organizationId,
        Long employeeId,
        Long shiftId,
        LocalDate workDate
) {
}
