package com.sassmodel.mapper;

import com.sassmodel.dto.MenuCategoryResponse;
import com.sassmodel.entity.MenuCategory;

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
