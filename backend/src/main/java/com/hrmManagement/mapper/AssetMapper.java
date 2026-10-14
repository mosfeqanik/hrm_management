package com.hrmManagement.mapper;

import com.hrmManagement.dto.AssetResponse;
import com.hrmManagement.entity.Asset;

public final class AssetMapper {

    private AssetMapper() {
    }

    public static AssetResponse toResponse(Asset entity) {
        return new AssetResponse(
                entity.getId(),
                entity.getOrganization().getId(),
                entity.getName(),
                entity.getAssetTag(),
                entity.getSerialNumber(),
                entity.getCategory(),
                entity.getDescription(),
                entity.getStatus()
        );
    }
}
