package com.sassmodel.mapper;

import com.sassmodel.dto.EmployeeResponse;
import com.sassmodel.entity.Employee;

public final class EmployeeMapper {

    private EmployeeMapper() {
    }

    public static EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getOrganization().getId(),
                employee.getUser() != null ? employee.getUser().getId() : null,
                employee.getFullName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getPosition(),
                employee.getJoinedAt(),
                employee.isActive()
        );
    }
}
