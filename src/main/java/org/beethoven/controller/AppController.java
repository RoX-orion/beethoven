package org.beethoven.controller;

import jakarta.annotation.Resource;
import org.beethoven.lib.annotation.Permission;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.vo.AppConfig;
import org.beethoven.pojo.vo.MusicConfig;
import org.beethoven.service.AppService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-12-01
 */

@RestController
@RequestMapping("app")
public class AppController {

    @Resource
    private AppService appService;

    @RequestMapping(value = "appConfig", method = RequestMethod.GET)
    public ApiResult<AppConfig> getAppConfig() {
        AppConfig appConfig = appService.getAppConfig();

        return ApiResult.ok(appConfig);
    }

    @RequestMapping(value = "musicConfig", method = RequestMethod.GET)
    public ApiResult<MusicConfig> getMusicConfig() {
        MusicConfig musicConfig = appService.getMusicConfig();

        return ApiResult.ok(musicConfig);
    }

    @Permission
    @RequestMapping(value = "musicConfig", method = RequestMethod.PUT)
    public ApiResult<Void> updateMusicConfig(MusicConfig musicConfig) {
        appService.updateMusicConfig(musicConfig);

        return ApiResult.ok();
    }
}
