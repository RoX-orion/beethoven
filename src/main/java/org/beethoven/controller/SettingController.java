package org.beethoven.controller;

import jakarta.annotation.Resource;
import org.beethoven.lib.annotation.Permission;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.entity.Setting;
import org.beethoven.service.SettingService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-20
 */

@RestController
@RequestMapping("setting")
public class SettingController {

    @Resource
    private SettingService settingService;

    @RequestMapping(value = "getSetting", method = RequestMethod.GET)
    public ApiResult<Setting> getSetting() {
        Setting setting = settingService.getSetting();

        return ApiResult.ok(setting);
    }

    @Permission
    @RequestMapping(value = "updateSetting", method = RequestMethod.PUT)
    public ApiResult<Void> updateSetting(@RequestBody Setting setting) {
        settingService.updateSetting(setting);

        return ApiResult.ok();
    }
}
