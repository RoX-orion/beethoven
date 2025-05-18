package org.beethoven.lib.store;

import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Copyright (c) 2025 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2025-05-12
 */

@Component
public class StorageTask {

    @Resource
    private StorageContext storageContext;

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteUselessFiles() {

    }
}
