package org.beethoven.service;

import jakarta.annotation.Resource;
import org.beethoven.mapper.ConfigMapper;
import org.beethoven.pojo.vo.AppConfig;
import org.springframework.stereotype.Service;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-12-01
 */

@Service
public class AppService {

    @Resource
    private ConfigMapper configMapper;

    public AppConfig getAppConfig() {

        AppConfig appConfig = new AppConfig();
        return appConfig;
    }
}
