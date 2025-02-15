package org.beethoven.lib.handler;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-02-15
 */

@Configuration
public class HandlerConfig implements WebMvcConfigurer {

    @Resource
    private PermissionHandler permissionHandler;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionHandler).addPathPatterns("/**");
    }
}
