package com.hrmManagement.dto;

import com.hrmManagement.entity.enums.*;

import java.math.BigDecimal;
import java.time.*;

public record DepartmentResponse(
        Long id,
        Long organizationId,
        String name,
        Long branchId,
        Long headId,
        boolean active
) {
}
