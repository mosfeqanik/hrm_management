package com.sassmodel.dto;

import com.sassmodel.entity.enums.PermissionAction;

public record PermissionResponse(
        Long id,
        String module,
        PermissionAction action
) {
}
