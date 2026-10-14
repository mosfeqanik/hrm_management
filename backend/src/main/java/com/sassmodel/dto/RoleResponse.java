package com.sassmodel.dto;

import com.sassmodel.entity.enums.RoleScope;

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
