package org.beethoven.service;

import jakarta.annotation.Resource;
import org.beethoven.lib.Constant;
import org.beethoven.lib.GlobalConfig;
import org.beethoven.lib.InitGlobal;
import org.beethoven.lib.store.StorageContext;
import org.beethoven.mapper.ConfigMapper;
import org.beethoven.pojo.entity.Config;
import org.beethoven.pojo.vo.AppConfig;
import org.beethoven.pojo.vo.MusicConfig;
import org.beethoven.util.Helpers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    @Resource
    private StorageContext storageContext;

    @Resource
    private InitGlobal initGlobal;

    public AppConfig getAppConfig() {
        AppConfig appConfig = new AppConfig();
        appConfig.setDefaultMusicCover(storageContext.getURL(GlobalConfig.defaultMusicCover));
        appConfig.setShardingSize(GlobalConfig.shardingSize);

        return appConfig;
    }

    public MusicConfig getMusicConfig() {
        MusicConfig musicConfig = new MusicConfig();
        musicConfig.setShardingSize(GlobalConfig.shardingSize);
        musicConfig.setDefaultMusicCover(storageContext.getURL(GlobalConfig.defaultMusicCover));

        return musicConfig;
    }

    @Transactional
    public void updateMusicConfig(MusicConfig musicConfig) {
        MultipartFile defaultMusicCoverFile = musicConfig.getDefaultMusicCoverFile();
        if (defaultMusicCoverFile != null) {
            String fileName = Helpers.buildOssFileName(defaultMusicCoverFile.getOriginalFilename());
            Config defaultMusicCoverConfig = new Config();
            defaultMusicCoverConfig.setConfigKey(Constant.DEFAULT_MUSIC_COVER);
            defaultMusicCoverConfig.setConfigValue(fileName);
            configMapper.insertOrUpdate(defaultMusicCoverConfig);

            try {
                storageContext.upload(defaultMusicCoverFile.getInputStream(), fileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Config shardingSize = new Config();
        shardingSize.setConfigKey(Constant.SHARDING_SIZE);
        shardingSize.setConfigValue(String.valueOf(musicConfig.getShardingSize()));
        configMapper.insertOrUpdate(shardingSize);

        initGlobal.run(null);
    }
}
