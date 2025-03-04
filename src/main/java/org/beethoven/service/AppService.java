package org.beethoven.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        Config shardingSize = configMapper.selectOne(new LambdaQueryWrapper<Config>().eq(Config::getConfigKey, Constant.SHARDING_SIZE));
        shardingSize = shardingSize == null ? new Config() : shardingSize;
        shardingSize.setConfigKey(Constant.SHARDING_SIZE);
        shardingSize.setConfigValue(musicConfig.getShardingSize() == null ? String.valueOf(Constant.DEFAULT_SHARDING_SIZE) : String.valueOf(musicConfig.getShardingSize()));
        configMapper.insertOrUpdate(shardingSize);

        MultipartFile defaultMusicCoverFile = musicConfig.getDefaultMusicCoverFile();
        if (defaultMusicCoverFile != null) {
            String fileName = Constant.SYSTEM + Helpers.buildOssFileName(defaultMusicCoverFile.getOriginalFilename());
            Config defaultMusicCoverConfig = configMapper.selectOne(new LambdaQueryWrapper<Config>().eq(Config::getConfigKey, Constant.DEFAULT_MUSIC_COVER));
            defaultMusicCoverConfig = defaultMusicCoverConfig == null ? new Config() : defaultMusicCoverConfig;
            String oldFileName = defaultMusicCoverConfig.getConfigValue();
            defaultMusicCoverConfig.setConfigKey(Constant.DEFAULT_MUSIC_COVER);
            defaultMusicCoverConfig.setConfigValue(fileName);
            configMapper.insertOrUpdate(defaultMusicCoverConfig);

            try {
                storageContext.upload(defaultMusicCoverFile.getInputStream(), fileName);
                storageContext.remove(oldFileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        initGlobal.run(null);
    }
}
