package com.hrmManagement.mapper;

import com.hrmManagement.dto.OrganizationSettingResponse;
import com.hrmManagement.entity.OrganizationSetting;

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
