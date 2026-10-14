package com.hrmManagement.mapper;

import com.hrmManagement.dto.MenuCategoryResponse;
import com.hrmManagement.entity.MenuCategory;

public final class MenuCategoryMapper {

    private MenuCategoryMapper() {
    }

    public static MenuCategoryResponse toResponse(MenuCategory category) {
        return new MenuCategoryResponse(
                category.getId(),
                category.getOrganization().getId(),
                category.getName(),
                category.getSortOrder(),
                category.isActive()
        );
    }
}
