package com.hrmManagement.mapper;

import com.hrmManagement.dto.ShiftResponse;
import com.hrmManagement.entity.Shift;

public final class ShiftMapper {

    private ShiftMapper() {
    }

    public static ShiftResponse toResponse(Shift entity) {
        return new ShiftResponse(
                entity.getId(),
                entity.getOrganization().getId(),
                entity.getName(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.isActive()
        );
    }
}
