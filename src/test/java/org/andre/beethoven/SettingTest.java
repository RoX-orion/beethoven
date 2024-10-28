package org.andre.beethoven;

import jakarta.annotation.Resource;
import org.beethoven.BeethovenApplication;
import org.beethoven.pojo.entity.Setting;
import org.beethoven.service.SettingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-20
 */

@SpringBootTest(classes = BeethovenApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SettingTest {

    @Resource
    private SettingService settingService;

    @Test
    public void getSetting() {
        Setting setting = settingService.getSetting();
        System.out.println(setting);
    }
}
