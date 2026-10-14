package com.sassmodel.mapper;

import com.sassmodel.dto.OrganizationSettingResponse;
import com.sassmodel.entity.OrganizationSetting;

public final class OrganizationSettingMapper {

    private OrganizationSettingMapper() {
    }

    public static OrganizationSettingResponse toResponse(OrganizationSetting setting) {
        return new OrganizationSettingResponse(
                setting.getId(),
                setting.getOrganization().getId(),
                setting.getKey(),
                setting.getValue()
        );
    }
}
