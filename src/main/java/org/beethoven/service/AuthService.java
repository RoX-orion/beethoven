package org.beethoven.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.beethoven.lib.Constant;
import org.beethoven.lib.exception.AuthenticationException;
import org.beethoven.mapper.AccountMapper;
import org.beethoven.pojo.OAuth2Info;
import org.beethoven.pojo.dto.OAuth2Login;
import org.beethoven.pojo.entity.Account;
import org.beethoven.pojo.enums.UserType;
import org.beethoven.pojo.vo.AccountVo;
import org.beethoven.service.GitHubOAuthClient.GitHubEmail;
import org.beethoven.service.GitHubOAuthClient.GitHubUser;
import org.beethoven.util.Helpers;
import org.beethoven.util.RequestUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-28
 */

@Service
public class AuthService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private GitHubOAuthClient gitHubOAuthClient;

    @Resource
    private ObjectMapper mapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private AccountService accountService;

    @Resource
    private SettingService settingService;

    @Value("${oauth2.github.client-id}")
    private String clientId;

    @Value("${oauth2.github.redirect-uri}")
    private String redirectUri;

    @Value("${oauth2.github.secret}")
    private String secret;

    public OAuth2Info getOAuth2Info(UserType userType) {
        OAuth2Info oauth2Info = new OAuth2Info();
        if (UserType.GITHUB == userType) {
            oauth2Info.setClientId(clientId);
            oauth2Info.setRedirectUri(redirectUri);
            oauth2Info.setState(Helpers.getRandomString(6));
        }
        return oauth2Info;
    }

    @Transactional
    public AccountVo oauth2Login(OAuth2Login oauth2Login) throws JsonProcessingException {
        Account account = new Account();
        AccountVo accountVo = new AccountVo();
        if (oauth2Login.getType() == UserType.GITHUB) {
            account.setUserType(UserType.GITHUB);
            if (!StringUtils.hasText(secret) || !StringUtils.hasText(clientId)) {
                throw new AuthenticationException("Load login info error!");
            }
            String accessToken = gitHubOAuthClient.exchangeAccessToken(clientId, secret, oauth2Login.getCode());

            GitHubUser user = gitHubOAuthClient.getUser(accessToken);
            account.setUsername(user.name());
            account.setEmail(user.email());
            account.setAvatar(user.avatarUrl());

            if (!StringUtils.hasText(account.getEmail())) {
                for (GitHubEmail email : gitHubOAuthClient.getEmails(accessToken)) {
                    if (email.primary()) {
                        account.setEmail(email.email());
                        break;
                    }
                }
            }

            if (!StringUtils.hasText(account.getEmail())) {
                throw new AuthenticationException("Can't get your email address!");
            }
            Account localAccount = accountMapper.selectOne(new LambdaQueryWrapper<Account>()
                    .eq(Account::getUserType, UserType.GITHUB)
                    .eq(Account::getEmail, account.getEmail())
            );

            accountVo.setToken(generateToken());
            if (localAccount != null) {
                accountService.setAccountInfo(localAccount, accountVo);
            } else {
                account.setCreateTime(LocalDateTime.now());
                account.setUpdateTime(LocalDateTime.now());
                accountMapper.insert(account);

                settingService.addSetting(account.getId());

                accountService.setAccountInfo(account, accountVo);
            }
        }
        redisTemplate.opsForValue().set(Constant.PREFIX.USER_INFO + accountVo.getToken(), mapper.writeValueAsString(accountVo), Constant.TOKEN_EXPIRE_TIME, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(Constant.PREFIX.USER_ID + accountVo.getToken(), String.valueOf(accountVo.getId()), Constant.TOKEN_EXPIRE_TIME, TimeUnit.DAYS);

        return accountVo;
    }

    public String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public void logout(HttpServletRequest request) {
        String token = RequestUtil.getToken(request);
        if (StringUtils.hasText(token)) {
            this.removeToken(token);
        }
    }

    public void removeToken(String token) {
        redisTemplate.delete(Constant.PREFIX.USER_INFO + token);
        redisTemplate.delete(Constant.PREFIX.USER_ID + token);
    }
}
