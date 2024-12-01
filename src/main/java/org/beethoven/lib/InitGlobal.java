package org.beethoven.lib;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.beethoven.lib.store.StorageContext;
import org.beethoven.mapper.ConfigMapper;
import org.beethoven.pojo.entity.Config;
import org.beethoven.service.StorageService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-03
 */

@Slf4j
@Configuration
public class InitGlobal implements ApplicationRunner {

    @Resource
    private ConfigMapper configMapper;

    @Resource
    private StorageService storageService;

    @Resource
    private StorageContext storageContext;

    @Override
    public void run(ApplicationArguments args) {
        Config shardingSizeConfig = configMapper.selectOne(
                new LambdaQueryWrapper<Config>().eq(Config::getConfigKey, Constant.SHARDING_CONFIG_KEY)
        );
        if (shardingSizeConfig != null && StringUtils.hasText(shardingSizeConfig.getConfigKey())) {
            try {
                GlobalConfig.shardingSize = Integer.parseInt(shardingSizeConfig.getConfigValue());
            } catch (Exception e) {
                log.error("Init music file sharding size fail!");
                log.error(e.getMessage());
                System.exit(-1);
            }
            log.info("Init music file sharding size successfully!");
        } else {
            log.error("Music file sharding size can't be null!");
            System.exit(-1);
        }

        try {
            storageService.refreshStorageConfig(Constant.DEFAULT_STORAGE);
            storageContext.refresh(Constant.DEFAULT_STORAGE);
            log.info("Init storage configuration successfully!");
        } catch (Exception e) {
            log.error("Init storage configuration fail!");
            log.error(e.getMessage());
        }
    }
}
