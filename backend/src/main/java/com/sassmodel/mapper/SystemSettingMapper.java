package com.sassmodel.mapper;

import com.sassmodel.dto.SystemSettingResponse;
import com.sassmodel.entity.SystemSetting;

public final class SystemSettingMapper {

    private SystemSettingMapper() {
    }

    public static SystemSettingResponse toResponse(SystemSetting setting) {
        return new SystemSettingResponse(
                setting.getId(),
                setting.getKey(),
                setting.getValue(),
                setting.getDescription()
        );
    }
}
