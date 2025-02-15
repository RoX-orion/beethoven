package org.beethoven.lib.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.beethoven.lib.annotation.Permission;
import org.beethoven.pojo.entity.ApiResult;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-02-15
 */

@Component
public class PermissionHandler implements HandlerInterceptor {

    @Resource
    private ObjectMapper mapper;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            Permission permission = handlerMethod.getMethodAnnotation(Permission.class);
            if (permission != null) {
                String token = request.getHeader("Authorization");
                if (!StringUtils.hasText(token)) {
                    write(response, ApiResult.expired("UNAUTHORIZED"));
                    return false;
                }
//                String value = annotation.value();
//                // 根据注解的值进行相应的处理
//                System.out.println("Annotation value: " + value);
            }
        }
        return true;
    }

    private void write(HttpServletResponse response, ApiResult<String> apiResult) {
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.println(mapper.writeValueAsString(apiResult));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
