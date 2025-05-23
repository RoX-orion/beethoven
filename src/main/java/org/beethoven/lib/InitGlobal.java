package org.beethoven.lib;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.beethoven.lib.store.StorageContext;
import org.beethoven.mapper.ConfigMapper;
import org.beethoven.pojo.OAuth2Info;
import org.beethoven.pojo.entity.Config;
import org.beethoven.service.StorageService;
import org.beethoven.util.Helpers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description: Init application
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

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private ObjectMapper mapper;

    @Value("${oauth2.github.client-id}")
    private String clientId;

    @Value("${oauth2.github.redirect-uri}")
    private String redirectUri;

    @Override
    public void run(ApplicationArguments args) {
        Config shardingSizeConfig = configMapper.selectOne(
                new LambdaQueryWrapper<Config>().eq(Config::getConfigKey, Constant.SHARDING_SIZE)
        );
        if (shardingSizeConfig != null && StringUtils.hasText(shardingSizeConfig.getConfigKey())) {
            try {
                GlobalConfig.shardingSize = Integer.parseInt(shardingSizeConfig.getConfigValue());
            } catch (Exception e) {
                log.error("Init music file sharding size fail!");
                log.error(e.getMessage());
            }
        } else {
            log.warn("Music file sharding size is null, use default sharding size.");
        }
        log.info("Init music file sharding size successfully!");

        try {
            storageService.refreshStorageConfig(Constant.DEFAULT_STORAGE);
            storageContext.refresh(Constant.DEFAULT_STORAGE);
            log.info("Init storage configuration successfully!");
        } catch (Exception e) {
            log.error("Init storage configuration fail!");
            log.error(e.getMessage());
        }

        Config defaultMusicCover = configMapper.selectOne(
                new LambdaQueryWrapper<Config>().eq(Config::getConfigKey, Constant.DEFAULT_MUSIC_COVER)
        );
        if (defaultMusicCover != null && StringUtils.hasText(defaultMusicCover.getConfigValue())) {
            GlobalConfig.defaultMusicCover = defaultMusicCover.getConfigValue();
        }

        OAuth2Info oauth2Info = new OAuth2Info();
        oauth2Info.setClientId(clientId);
        oauth2Info.setRedirectUri(redirectUri);
        oauth2Info.setState(Helpers.getRandomString(6));

        try {
            redisTemplate.opsForValue().set(Constant.PREFIX.CONFIG + "oauth2Info", mapper.writeValueAsString(oauth2Info));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
