package com.hrmManagement.mapper;

import com.hrmManagement.dto.DepartmentResponse;
import com.hrmManagement.entity.Department;

public final class DepartmentMapper {

    private DepartmentMapper() {
    }

    public static DepartmentResponse toResponse(Department entity) {
        return new DepartmentResponse(
                entity.getId(),
                entity.getOrganization().getId(),
                entity.getName(),
                entity.getBranch() != null ? entity.getBranch().getId() : null,
                entity.getHead() != null ? entity.getHead().getId() : null,
                entity.isActive()
        );
    }
}
