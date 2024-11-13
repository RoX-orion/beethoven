package org.beethoven.service;

import jakarta.annotation.Resource;
import org.beethoven.mapper.AccountMapper;
import org.beethoven.pojo.entity.Account;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.vo.AccountVo;
import org.beethoven.util.Helpers;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-11-13
 */

@Service
public class AccountService {

    @Resource
    private AccountMapper accountMapper;

    public ApiResult<AccountVo> getAccountInfo(Integer userId) {
        Account account = accountMapper.selectById(userId);
        AccountVo accountVo = new AccountVo();
        BeanUtils.copyProperties(account, accountVo);
        accountVo.setAvatar(Helpers.buildFullOssLink(accountVo.getAvatar()));

        return ApiResult.ok(accountVo);
    }
}
