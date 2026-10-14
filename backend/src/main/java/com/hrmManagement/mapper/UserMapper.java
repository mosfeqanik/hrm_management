package com.hrmManagement.mapper;

import com.hrmManagement.dto.RoleSummary;
import com.hrmManagement.dto.UserResponse;
import com.hrmManagement.entity.User;

import java.util.stream.Collectors;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getOrganization() != null ? user.getOrganization().getId() : null,
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getUserType(),
                user.isActive(),
                user.getLastLoginAt(),
                user.getRoles().stream()
                        .map(role -> new RoleSummary(role.getId(), role.getName()))
                        .collect(Collectors.toSet())
        );
    }
}
