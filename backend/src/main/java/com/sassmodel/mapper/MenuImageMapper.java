package com.sassmodel.mapper;

import com.sassmodel.dto.MenuImageResponse;
import com.sassmodel.entity.MenuImage;

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
