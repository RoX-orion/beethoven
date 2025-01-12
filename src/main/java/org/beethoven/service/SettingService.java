package org.beethoven.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.beethoven.lib.exception.BeethovenException;
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

    public void addSetting(Long userId) {
        if (userId == null) {
            throw new BeethovenException("User id can't be null when create setting!");
        }
        if (!settingMapper.exists(new LambdaQueryWrapper<Setting>().eq(Setting::getUserId, userId))) {
            Setting setting = new Setting();
            setting.setUserId(userId);
            setting.setVolume(0);
            setting.setCurrentTime(0);
            setting.setMusicId(null);
            setting.setIsMute(true);
            setting.setPlayMode("loop");

            settingMapper.insert(setting);
        }
    }
}
