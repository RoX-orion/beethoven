package org.beethoven.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.beethoven.pojo.OAuth2Info;
import org.beethoven.pojo.dto.OAuth2Login;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.enums.UserType;
import org.beethoven.pojo.vo.AccountVo;
import org.beethoven.service.AuthService;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-12-08
 */

@RestController
@RequestMapping("auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @RequestMapping(value = "oauth2/info", method = RequestMethod.GET)
    public ApiResult<OAuth2Info> getOAuth2Info(@RequestParam("type") UserType userType) throws JsonProcessingException {
        OAuth2Info oAuth2Info = authService.getOAuth2Info(userType);

        return ApiResult.ok(oAuth2Info);
    }

    @RequestMapping("oauth2/login")
    public ApiResult<AccountVo> oauth2Login(@RequestBody @Valid OAuth2Login oauth2Login) throws JsonProcessingException {
        AccountVo accountVo = authService.oauth2Login(oauth2Login);

        return ApiResult.ok(accountVo);
    }

    @RequestMapping(value = "oauth/github", method = RequestMethod.GET)
    public void oauthGithub(Object object) {
        System.out.println(object);
    }

    @RequestMapping(value = "oauth/github/hook", method = RequestMethod.POST)
    public void githubHook(@RequestBody Object object) {
        System.out.println("event:" + object);
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public ApiResult<Void> logout(HttpServletRequest request) {
        authService.logout(request);

        return ApiResult.ok();
    }
}
