package com.hrmManagement.dto;

import java.time.LocalDate;

public record EmployeeResponse(
        Long id,
        Long organizationId,
        Long userId,
        String fullName,
        String email,
        String phone,
        String position,
        LocalDate joinedAt,
        boolean active
) {
}
