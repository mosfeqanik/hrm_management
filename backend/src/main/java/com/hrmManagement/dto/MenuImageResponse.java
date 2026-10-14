package com.hrmManagement.dto;

public record MenuImageResponse(
        Long id,
        Long organizationId,
        String imageUrl,
        String caption,
        int sortOrder
) {
}
