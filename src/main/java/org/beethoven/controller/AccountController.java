package org.beethoven.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.vo.AccountVo;
import org.beethoven.service.AccountService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-13
 */

@RestController
@RequestMapping("account")
public class AccountController {

    @Resource
    private AccountService accountService;

    @RequestMapping(value = "info/{id}", method = RequestMethod.GET)
    public ApiResult<AccountVo> getAccountInfo(@PathVariable("id") Long id) {
        return accountService.getAccountInfo(id);
    }

    @RequestMapping(value = "info/token", method = RequestMethod.GET)
    public ApiResult<AccountVo> getAccountInfoByToken(HttpServletRequest request) throws JsonProcessingException {
        AccountVo accountVo = accountService.getAccountInfoByToken(request);

        return ApiResult.ok(accountVo);
    }
}
