package com.sassmodel.mapper;

import com.sassmodel.dto.MenuItemResponse;
import com.sassmodel.entity.MenuItem;

public final class MenuItemMapper {

    private MenuItemMapper() {
    }

    public static MenuItemResponse toResponse(MenuItem item) {
        return new MenuItemResponse(
                item.getId(),
                item.getOrganization().getId(),
                item.getCategory().getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getImageUrl(),
                item.isAvailable()
        );
    }
}
