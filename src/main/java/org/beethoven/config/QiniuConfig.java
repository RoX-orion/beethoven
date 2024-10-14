package org.beethoven.config;

import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-09
 */

@Configuration
public class QiniuConfig {

    @Value("${oss.qiniu.access-key}")
    private String accessKey;

    @Value("${oss.qiniu.secret-key}")
    private String secretKey;

    @Bean
    public UploadManager initUploadManager() {
        com.qiniu.storage.Configuration config = new com.qiniu.storage.Configuration();
        config.resumableUploadAPIVersion = com.qiniu.storage.Configuration.ResumableUploadAPIVersion.V2;

        return new UploadManager(config);
    }

    @Bean
    public Auth initAuth() {
        return Auth.create(accessKey, secretKey);
    }
}
