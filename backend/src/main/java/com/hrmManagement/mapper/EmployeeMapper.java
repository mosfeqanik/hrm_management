package com.hrmManagement.mapper;

import com.hrmManagement.dto.EmployeeResponse;
import com.hrmManagement.entity.Employee;

public final class EmployeeMapper {

    private EmployeeMapper() {
    }

    public static EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getOrganization().getId(),
                employee.getUser() != null ? employee.getUser().getId() : null,
                employee.getEmployeeCode(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getPosition(),
                employee.getNationalIdSerial(),
                employee.getPhotoUrl(),
                employee.getAddress(),
                employee.getEmergencyContactName(),
                employee.getEmergencyContactPhone(),
                employee.getEmergencyContactRelation(),
                employee.getBranch() != null ? employee.getBranch().getId() : null,
                employee.getDepartment() != null ? employee.getDepartment().getId() : null,
                employee.getManager() != null ? employee.getManager().getId() : null,
                employee.getEmploymentType(),
                employee.getJoinedAt(),
                employee.isActive()
        );
    }
}
