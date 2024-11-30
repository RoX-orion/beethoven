package org.beethoven.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.beethoven.pojo.dto.StorageDTO;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.entity.Storage;
import org.beethoven.pojo.vo.CommonVo;
import org.beethoven.service.StorageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-30
 */

@RestController
@RequestMapping("/storage")
public class StorageController {

    @Resource
    private StorageService storageService;

    @RequestMapping(value = "getAvailableStorage", method = RequestMethod.GET)
    public ApiResult<List<CommonVo<String, String>>> getAvailableStorage() {
        List<CommonVo<String, String>> availableStorageList = storageService.getAvailableStorage();

        return ApiResult.ok(availableStorageList);
    }

    @RequestMapping(value = "info/{provider}", method = RequestMethod.GET)
    public ApiResult<Storage> getStorageInfo(@PathVariable("provider") String provider) {
        Storage storageInfo = storageService.getStorageInfo(provider);

        return ApiResult.ok(storageInfo);
    }

    @RequestMapping(value = "configureStorage", method = RequestMethod.POST)
    public ApiResult<Void> configureStorage(@RequestBody @Valid StorageDTO storageDTO) {
        storageService.configureStorage(storageDTO);

        return ApiResult.ok();
    }
}
