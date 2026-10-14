package com.hrmManagement.mapper;

import com.hrmManagement.dto.ShiftAssignmentResponse;
import com.hrmManagement.entity.ShiftAssignment;

public final class ShiftAssignmentMapper {

    private ShiftAssignmentMapper() {
    }

    public static ShiftAssignmentResponse toResponse(ShiftAssignment entity) {
        return new ShiftAssignmentResponse(
                entity.getId(),
                entity.getOrganization().getId(),
                entity.getEmployee().getId(),
                entity.getShift() != null ? entity.getShift().getId() : null,
                entity.getWorkDate()
        );
    }
}
