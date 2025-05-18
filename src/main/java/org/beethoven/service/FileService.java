package org.beethoven.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.beethoven.mapper.FileInfoMapper;
import org.beethoven.pojo.entity.FileInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-05-17
 */

@Service
public class FileService {

    @Resource
    private FileInfoMapper fileInfoMapper;

    public List<String> getHashList(String hash) {
        FileInfo fileInfo = fileInfoMapper.selectOne(new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getChecksum, hash));
        if (fileInfo != null) {
            return fileInfo.getHashList();
        } else {
            return List.of();
        }
    }
}
