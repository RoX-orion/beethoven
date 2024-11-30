package org.beethoven.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.beethoven.mapper.StorageMapper;
import org.beethoven.pojo.dto.StorageDTO;
import org.beethoven.pojo.entity.Storage;
import org.beethoven.pojo.enums.OssProvider;
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
        OssProvider[] values = OssProvider.values();
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
        OssProvider provider = OssProvider.getProvider(storageDTO.getProvider());
        Storage storage = new Storage();
        BeanUtils.copyProperties(storageDTO, storage);
        storage.setProvider(provider);
        storageMapper.insertOrUpdate(storage);
    }
}
