package org.beethoven.service;

import jakarta.annotation.Resource;
import org.beethoven.mapper.SettingMapper;
import org.beethoven.pojo.entity.Setting;
import org.springframework.stereotype.Service;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-20
 */

@Service
public class SettingService {

    @Resource
    private SettingMapper settingMapper;

    public Setting getSetting() {
        return settingMapper.selectById(1);
    }

    public void updateSetting(Setting setting) {
        if (setting.getUserId() == null) return;
        settingMapper.updateById(setting);
    }
}
