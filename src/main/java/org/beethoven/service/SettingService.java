package org.beethoven.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.beethoven.lib.AuthContext;
import org.beethoven.lib.exception.BeethovenException;
import org.beethoven.mapper.SettingMapper;
import org.beethoven.pojo.entity.Setting;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    private AuthContext authContext;

    public Setting getSetting() {
        return settingMapper.selectById(authContext.getUserId());
    }

    public void updateSetting(Setting setting) {
        if (!StringUtils.hasText(setting.getUserId())) return;
        settingMapper.insertOrUpdate(setting);
    }

    public void addSetting(String userId) {
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
