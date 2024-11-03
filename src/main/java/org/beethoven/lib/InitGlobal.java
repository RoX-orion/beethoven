package org.beethoven.lib;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.beethoven.mapper.ConfigMapper;
import org.beethoven.pojo.entity.Config;
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
    private GlobalConfig globalConfig;

    @Override
    public void run(ApplicationArguments args) {
        Config config = configMapper.selectOne(
                new LambdaQueryWrapper<Config>().eq(Config::getConfigKey, Constant.SHARDING_CONFIG_KEY)
        );
        if (config != null && StringUtils.hasText(config.getConfigKey())) {
            try {
                globalConfig.shardingSize = Integer.parseInt(config.getConfigValue());
            } catch (Exception e) {
                log.error("Init music file sharding size fail!");
                log.error(e.getMessage());
            }
            log.info("Init music file sharding size successfully!");
        }
    }
}
