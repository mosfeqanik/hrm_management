package com.sassmodel.mapper;

import com.sassmodel.dto.PermissionResponse;
import com.sassmodel.entity.Permission;

public final class PermissionMapper {

    private PermissionMapper() {
    }

    public static PermissionResponse toResponse(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getModule(),
                permission.getAction()
        );
    }
}
