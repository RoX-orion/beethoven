package org.beethoven.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import okhttp3.*;
import org.beethoven.lib.Constant;
import org.beethoven.lib.exception.AuthenticationException;
import org.beethoven.mapper.AccountMapper;
import org.beethoven.pojo.OAuth2Info;
import org.beethoven.pojo.dto.OAuth2Login;
import org.beethoven.pojo.entity.Account;
import org.beethoven.pojo.enums.UserType;
import org.beethoven.pojo.vo.AccountVo;
import org.beethoven.util.Helpers;
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
    private OkHttpClient httpClient;

    @Resource
    private ObjectMapper mapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private AccountService accountService;

    @Resource
    private SettingService settingService;

    public Integer getUserId() {
        return 1;
    }

    public OAuth2Info getOAuth2Info(UserType userType) throws JsonProcessingException {
        if (UserType.GITHUB == userType) {
            String value = redisTemplate.opsForValue().get(Constant.PREFIX.CONFIG + "oauth2Info");
            OAuth2Info oauth2Info = mapper.readValue(value, new TypeReference<>() {
            });
            if (oauth2Info != null) {
                oauth2Info.setState(Helpers.getRandomString(6));
            }
            return oauth2Info;
        }
        return null;
    }

    @Transactional
    public AccountVo oauth2Login(OAuth2Login oauth2Login) throws JsonProcessingException {
        Account account = new Account();
        AccountVo accountVo = new AccountVo();
        if (oauth2Login.getType() == UserType.GITHUB) {
            account.setUserType(UserType.GITHUB);
            String secret = redisTemplate.opsForValue().get(Constant.PREFIX.GITHUB_CLIENT_SECRET);
            String value = redisTemplate.opsForValue().get(Constant.PREFIX.CONFIG + "oauth2Info");
            OAuth2Info oauth2Info = mapper.readValue(value, new TypeReference<>() {});
            if (!StringUtils.hasText(secret) || oauth2Info == null) {
                throw new AuthenticationException("Login error!");
            }
            RequestBody accessTokenRequestBody = new FormBody.Builder()
                    .add("client_id", oauth2Info.getClientId())
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
                    Map<String, String> accessTokenResponseBody = Helpers.getBodyAsMap(body.string());
                    accessToken = accessTokenResponseBody.get("access_token");
                    if (!StringUtils.hasText(accessToken)) {
                        throw new AuthenticationException("get access token error!");
                    }
                } else {
                    throw new AuthenticationException("get access token error!");
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new AuthenticationException("Login error!");
            }

            Request userInfoRequest = new Request.Builder()
                    .url("https://api.github.com/user")
                    .header("Authorization", "Bearer " + accessToken)
                    .get()
                    .build();
            try(Response userInfoResponse = httpClient.newCall(userInfoRequest).execute()) {
                if (userInfoResponse.isSuccessful()) {
                    Map<String, String> userInfoBody = mapper.readValue(userInfoResponse.body().string(), new TypeReference<>() {
                    });
                    account.setUsername(userInfoBody.get("name"));
                    account.setEmail(userInfoBody.get("email"));
                    account.setAvatar(userInfoBody.get("avatar_url"));
                } else {
                    throw new AuthenticationException("get user info error!");
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new AuthenticationException("Login error!");
            }

            if (!StringUtils.hasText(account.getEmail())) {
                Request emailRequest = new Request.Builder()
                        .url("https://api.github.com/user/emails")
                        .header("Authorization", "Bearer " + accessToken)
                        .get()
                        .build();
                try(Response emailResponse = httpClient.newCall(emailRequest).execute()) {
                    if (emailResponse.isSuccessful()) {
                        List<Map<String, String>> emailList = mapper.readValue(emailResponse.body().string(), new TypeReference<>() {
                        });
                        for (Map<String, String> emailInfo : emailList) {
                            String primary = emailInfo.get("");
                            if (StringUtils.hasText(primary) && "true".equals(primary)) {
                                account.setEmail(emailInfo.get("email"));
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new AuthenticationException("Can't get your email address!");
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
        redisTemplate.opsForValue().set(Constant.PREFIX.USER_INFO + accountVo.getToken(), mapper.writeValueAsString(accountVo));

        return accountVo;
    }

    public String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
