package org.beethoven.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.beethoven.lib.Constant;
import org.beethoven.lib.exception.StorageException;
import org.beethoven.mapper.StorageMapper;
import org.beethoven.pojo.dto.StorageDTO;
import org.beethoven.pojo.entity.Storage;
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

    public Storage getStorageInfo(String provider) {
        return storageMapper.selectOne(
                new LambdaQueryWrapper<Storage>().eq(Storage::getProvider, provider)
        );
    }

    public void configureStorage(StorageDTO storageDTO) {
        StorageProvider provider = StorageProvider.getProvider(storageDTO.getProvider());
        Storage storage = new Storage();
        BeanUtils.copyProperties(storageDTO, storage);
        storage.setProvider(provider);
        storageMapper.insertOrUpdate(storage);
    }

    public void refreshStorageConfig(String provider) {
        Storage storage = storageMapper.selectOne(
                new LambdaQueryWrapper<Storage>().eq(Storage::getProvider, provider)
        );
        if (storage == null) {
            throw new StorageException("Not configure storage!");
        }
        Constant.ENDPOINT = storage.getEndpoint();
        Constant.setStorage(storage);
    }
}
