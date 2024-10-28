package org.beethoven.service;

import org.beethoven.mapper.SettingMapper;
import org.beethoven.pojo.entity.Setting;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final SettingMapper settingMapper;

    @Autowired
    public SettingService(
            final SettingMapper settingMapper
    ) {
        this.settingMapper = settingMapper;
    }

    public Setting getSetting() {
        return settingMapper.selectById(1);
    }
}
