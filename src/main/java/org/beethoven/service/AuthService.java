package org.beethoven.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.beethoven.lib.Constant;
import org.beethoven.lib.exception.AuthenticationException;
import org.beethoven.mapper.AccountMapper;
import org.beethoven.mapper.ConfigMapper;
import org.beethoven.pojo.OAuth2Info;
import org.beethoven.pojo.dto.OAuth2Login;
import org.beethoven.pojo.entity.Account;
import org.beethoven.pojo.enums.UserType;
import org.beethoven.pojo.vo.AccountVo;
import org.beethoven.util.Helpers;
import org.beethoven.util.RequestUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-28
 */

@Slf4j
@Service
public class AuthService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private OkHttpClient httpClient;

    @Resource
    private ObjectMapper mapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private AccountService accountService;

    @Resource
    private SettingService settingService;

    @Resource
    private ConfigMapper configMapper;

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
            RequestBody accessTokenRequestBody = new FormBody.Builder()
                    .add("client_id", clientId)
                    .add("client_secret", secret)
                    .add("code", oauth2Login.getCode())
                    .build();
            Request accessTokenRequest = new Request.Builder()
                    .url("https://github.com/login/oauth/access_token")
                    .post(accessTokenRequestBody)
                    .build();
            String accessToken;
            try (Response accessTokenResponse = httpClient.newCall(accessTokenRequest).execute()) {
                if (accessTokenResponse.isSuccessful()) {
                    ResponseBody body = accessTokenResponse.body();
                    if (body == null) {
                        throw new AuthenticationException("Get access token error: empty response body!");
                    }
                    Map<String, String> accessTokenResponseBody = Helpers.getBodyAsMap(body.string());
                    accessToken = accessTokenResponseBody.get("access_token");
                    if (!StringUtils.hasText(accessToken)) {
                        throw new AuthenticationException("Get access token error!");
                    }
                } else {
                    throw new AuthenticationException("Get access token error!");
                }
            } catch (IOException e) {
                log.error("fetch access token error", e);
                throw new AuthenticationException("Fetch access token error!");
            }

            Request userInfoRequest = new Request.Builder()
                    .url("https://api.github.com/user")
                    .header("Authorization", "Bearer " + accessToken)
                    .get()
                    .build();
            try(Response userInfoResponse = httpClient.newCall(userInfoRequest).execute()) {
                if (userInfoResponse.isSuccessful()) {
                    ResponseBody body = userInfoResponse.body();
                    if (body == null) {
                        throw new AuthenticationException("Get user info error: empty response body!");
                    }
                    Map<String, String> userInfoBody = mapper.readValue(body.string(), new TypeReference<>() {
                    });
                    account.setUsername(userInfoBody.get("name"));
                    account.setEmail(userInfoBody.get("email"));
                    account.setAvatar(userInfoBody.get("avatar_url"));
                } else {
                    throw new AuthenticationException("Get user info error!");
                }
            } catch (IOException e) {
                log.error("fetch user info error", e);
                throw new AuthenticationException("Fetch user info error!");
            }

            if (!StringUtils.hasText(account.getEmail())) {
                Request emailRequest = new Request.Builder()
                        .url("https://api.github.com/user/emails")
                        .header("Authorization", "Bearer " + accessToken)
                        .get()
                        .build();
                try(Response emailResponse = httpClient.newCall(emailRequest).execute()) {
                    if (emailResponse.isSuccessful()) {
                        ResponseBody body = emailResponse.body();
                        if (body == null) {
                            throw new AuthenticationException("Can't fetch your email address: empty response body!");
                        }
                        List<Map<String, String>> emailList = mapper.readValue(body.string(), new TypeReference<>() {
                        });
                        for (Map<String, String> emailInfo : emailList) {
                            String primary = emailInfo.get("primary");
                            if (StringUtils.hasText(primary) && "true".equals(primary)) {
                                account.setEmail(emailInfo.get("email"));
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("fetch email address error", e);
                    throw new AuthenticationException("Can't fetch your email address!");
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
