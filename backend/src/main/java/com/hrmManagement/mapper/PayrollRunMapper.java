package com.hrmManagement.mapper;

import com.hrmManagement.dto.PayrollRunResponse;
import com.hrmManagement.entity.PayrollRun;

public final class PayrollRunMapper {

    private PayrollRunMapper() {
    }

    public static PayrollRunResponse toResponse(PayrollRun entity) {
        return new PayrollRunResponse(
                entity.getId(),
                entity.getOrganization().getId(),
                entity.getYear(),
                entity.getMonth(),
                entity.getBranch() != null ? entity.getBranch().getId() : null,
                entity.getStatus(),
                entity.getNote()
        );
    }
}
