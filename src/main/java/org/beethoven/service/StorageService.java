package org.beethoven.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.beethoven.lib.GlobalConfig;
import org.beethoven.lib.exception.StorageException;
import org.beethoven.mapper.StorageMapper;
import org.beethoven.pojo.dto.StorageDTO;
import org.beethoven.pojo.entity.StorageInfo;
import org.beethoven.pojo.enums.StorageProvider;
import org.beethoven.pojo.vo.CommonVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-30
 */

@Service
public class StorageService {

    @Resource
    private StorageMapper storageMapper;

    public List<CommonVo<String, String>> getAvailableStorage() {
        StorageProvider[] values = StorageProvider.values();
        return Arrays.stream(values)
                .map(e -> new CommonVo<>(e.name(), e.getName()))
                .toList();
    }

    public StorageInfo getStorageInfo(String provider) {
        return storageMapper.selectOne(
                new LambdaQueryWrapper<StorageInfo>().eq(StorageInfo::getProvider, provider)
        );
    }

    public void configureStorage(StorageDTO storageDTO) {
        StorageProvider provider = StorageProvider.getProvider(storageDTO.getProvider());
        StorageInfo storageInfo = new StorageInfo();
        BeanUtils.copyProperties(storageDTO, storageInfo);
        storageInfo.setProvider(provider);
        storageMapper.insertOrUpdate(storageInfo);
    }

    public void refreshStorageConfig(StorageProvider provider) {
        StorageInfo storageInfo = storageMapper.selectOne(
                new LambdaQueryWrapper<StorageInfo>().eq(StorageInfo::getProvider, provider)
        );
        if (storageInfo == null) {
            throw new StorageException("Not configure storageInfo!");
        }
        GlobalConfig.endpoint = storageInfo.getEndpoint();
        GlobalConfig.setStorageInfo(storageInfo);
    }
}
