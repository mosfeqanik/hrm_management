package com.sassmodel.dto;

public record OrganizationSettingResponse(
        Long id,
        Long organizationId,
        String key,
        String value
) {
}
