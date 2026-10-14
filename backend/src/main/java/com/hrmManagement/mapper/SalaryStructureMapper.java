package com.hrmManagement.mapper;

import com.hrmManagement.dto.SalaryStructureResponse;
import com.hrmManagement.entity.SalaryStructure;

public final class SalaryStructureMapper {

    private SalaryStructureMapper() {
    }

    public static SalaryStructureResponse toResponse(SalaryStructure entity) {
        return new SalaryStructureResponse(
                entity.getId(),
                entity.getOrganization().getId(),
                entity.getEmployee().getId(),
                entity.getEffectiveFrom(),
                entity.getBasicSalary(),
                entity.getAllowances(),
                entity.getCurrency()
        );
    }
}
