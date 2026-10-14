package com.sassmodel.dto;

public record MenuImageResponse(
        Long id,
        Long organizationId,
        String imageUrl,
        String caption,
        int sortOrder
) {
}
