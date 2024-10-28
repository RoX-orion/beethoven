package org.beethoven.controller;

import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.entity.Setting;
import org.beethoven.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final SettingService settingService;

    @Autowired
    public SettingController(
            final SettingService settingService
    ) {
        this.settingService = settingService;
    }

    @RequestMapping(value = "getSetting", method = RequestMethod.GET)
    public ApiResult<Setting> getSetting() {
        Setting setting = settingService.getSetting();

        return ApiResult.ok(setting);
    }
}
