package com.hrmManagement.mapper;

import com.hrmManagement.dto.MenuImageResponse;
import com.hrmManagement.entity.MenuImage;

public final class MenuImageMapper {

    private MenuImageMapper() {
    }

    public static MenuImageResponse toResponse(MenuImage menuImage) {
        return new MenuImageResponse(
                menuImage.getId(),
                menuImage.getOrganization().getId(),
                menuImage.getImageUrl(),
                menuImage.getCaption(),
                menuImage.getSortOrder()
        );
    }
}
