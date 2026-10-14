package com.hrmManagement.mapper;

import com.hrmManagement.dto.AssetAssignmentResponse;
import com.hrmManagement.entity.AssetAssignment;

public final class AssetAssignmentMapper {

    private AssetAssignmentMapper() {
    }

    public static AssetAssignmentResponse toResponse(AssetAssignment entity) {
        return new AssetAssignmentResponse(
                entity.getId(),
                entity.getOrganization().getId(),
                entity.getAsset().getId(),
                entity.getEmployee().getId(),
                entity.getAssignedAt(),
                entity.getReturnedAt(),
                entity.getConditionNote()
        );
    }
}
