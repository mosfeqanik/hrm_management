package com.hrmManagement.mapper;

import com.hrmManagement.dto.LeaveRequestResponse;
import com.hrmManagement.entity.LeaveRequest;

public final class LeaveRequestMapper {

    private LeaveRequestMapper() {
    }

    public static LeaveRequestResponse toResponse(LeaveRequest entity) {
        return new LeaveRequestResponse(
                entity.getId(),
                entity.getOrganization().getId(),
                entity.getEmployee().getId(),
                entity.getLeaveType().getId(),
                entity.getFromDate(),
                entity.getToDate(),
                entity.getDays(),
                entity.getReason(),
                entity.getStatus(),
                entity.getDecidedBy() != null ? entity.getDecidedBy().getId() : null,
                entity.getDecidedAt(),
                entity.getDecisionNote()
        );
    }
}
