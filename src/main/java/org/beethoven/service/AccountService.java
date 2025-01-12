package org.beethoven.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.beethoven.lib.Constant;
import org.beethoven.lib.exception.AuthenticationException;
import org.beethoven.mapper.AccountMapper;
import org.beethoven.pojo.entity.Account;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.vo.AccountVo;
import org.beethoven.util.RequestUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private ObjectMapper mapper;

    public ApiResult<AccountVo> getAccountInfo(Long id) {
        Account account = accountMapper.selectById(id);
        AccountVo accountVo = new AccountVo();
        BeanUtils.copyProperties(account, accountVo);

        return ApiResult.ok(accountVo);
    }

    public AccountVo getAccountInfoByToken(HttpServletRequest request) throws JsonProcessingException {
        String token = RequestUtil.getToken(request);
        AccountVo accountVo = null;
        if (StringUtils.hasText(token)) {
            String value = redisTemplate.opsForValue().get(Constant.PREFIX.USER_INFO + token);
            if (StringUtils.hasText(value)) {
                accountVo = mapper.readValue(value, new TypeReference<>() {
                });
            }
        }
        if (accountVo == null) {
            throw new AuthenticationException("User info is null!");
        }
        return accountVo;
    }

    public void setAccountInfo(Account account, AccountVo accountVo) {
        accountVo.setId(account.getId());
        accountVo.setUsername(account.getUsername());
        accountVo.setAvatar(account.getAvatar());
        accountVo.setCreateTime(account.getCreateTime());
        accountVo.setUpdateTime(account.getUpdateTime());
    }
}
