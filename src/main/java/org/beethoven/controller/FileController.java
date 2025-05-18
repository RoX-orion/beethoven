package org.beethoven.controller;

import jakarta.annotation.Resource;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.service.FileService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-05-17
 */

@RestController
@RequestMapping("file")
public class FileController {

    @Resource
    private FileService fileService;

    @RequestMapping(value = "hash", method = RequestMethod.GET)
    public ApiResult<List<String>> getHash(@RequestParam("hash") String hash) {
        List<String> hashList = fileService.getHashList(hash);

        return ApiResult.ok(hashList);
    }
}
