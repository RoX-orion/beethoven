package org.beethoven.service;

import jakarta.annotation.Resource;
import org.beethoven.lib.GlobalConfig;
import org.beethoven.lib.store.StorageContext;
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

    @Resource
    private StorageContext storageContext;

    public Setting getSetting() {
        Setting setting = settingMapper.selectById(1);
        setting.setDefaultMusicCover(storageContext.getURL(GlobalConfig.defaultMusicCover));

        return setting;
    }
}
