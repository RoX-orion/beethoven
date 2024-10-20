package org.beethoven.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-20
 */

@Configuration
public class GlobalCorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")//添加映射路径，“/**”表示对所有的路径实行全局跨域访问权限的设置
                        .allowedOriginPatterns("*") //开放哪些ip、端口、域名的访问权限
                        .allowCredentials(true)//是否允许发送Cookie信息
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")//开放哪些Http方法，允许跨域访问
                        .allowedHeaders("*")//允许HTTP请求中的携带哪些Header信息
                        .exposedHeaders();//暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息
            }
        };
    }
}
