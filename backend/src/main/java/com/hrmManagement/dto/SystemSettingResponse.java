package com.hrmManagement.dto;

public record SystemSettingResponse(
        Long id,
        String key,
        String value,
        String description
) {
}
