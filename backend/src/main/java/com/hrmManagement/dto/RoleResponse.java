package com.hrmManagement.dto;

import com.hrmManagement.entity.enums.RoleScope;

import java.util.Set;

public record RoleResponse(
        Long id,
        Long organizationId,
        String name,
        String description,
        RoleScope scope,
        boolean system,
        Set<PermissionResponse> permissions
) {
}
