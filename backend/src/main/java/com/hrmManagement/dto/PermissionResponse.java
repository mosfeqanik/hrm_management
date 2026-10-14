package com.hrmManagement.dto;

import com.hrmManagement.entity.enums.PermissionAction;

public record PermissionResponse(
        Long id,
        String module,
        PermissionAction action
) {
}
