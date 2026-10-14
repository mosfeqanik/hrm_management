package com.hrmManagement.mapper;

import com.hrmManagement.dto.LeaveTypeResponse;
import com.hrmManagement.entity.LeaveType;

public final class LeaveTypeMapper {

    private LeaveTypeMapper() {
    }

    public static LeaveTypeResponse toResponse(LeaveType entity) {
        return new LeaveTypeResponse(
                entity.getId(),
                entity.getOrganization().getId(),
                entity.getName(),
                entity.getAnnualDays(),
                entity.isPaid(),
                entity.isActive()
        );
    }
}
