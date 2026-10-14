package com.sassmodel.mapper;

import com.sassmodel.dto.RoleResponse;
import com.sassmodel.entity.Role;

import java.util.stream.Collectors;

public final class RoleMapper {

    private RoleMapper() {
    }

    public static RoleResponse toResponse(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getOrganization() != null ? role.getOrganization().getId() : null,
                role.getName(),
                role.getDescription(),
                role.getScope(),
                role.isSystem(),
                role.getPermissions().stream()
                        .map(PermissionMapper::toResponse)
                        .collect(Collectors.toSet())
        );
    }
}
