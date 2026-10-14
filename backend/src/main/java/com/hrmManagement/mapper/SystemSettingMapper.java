package com.hrmManagement.mapper;

import com.hrmManagement.dto.SystemSettingResponse;
import com.hrmManagement.entity.SystemSetting;

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
