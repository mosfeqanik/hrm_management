package com.hrmManagement.mapper;

import com.hrmManagement.dto.PermissionResponse;
import com.hrmManagement.entity.Permission;

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
