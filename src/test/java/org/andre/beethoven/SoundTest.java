package org.andre.beethoven;

import jakarta.annotation.Resource;
import org.beethoven.service.MusicService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-09
 */

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SoundTest {

    @Resource
    private MusicService musicService;

    @Test
    public void upload() {
        musicService.uploadResource();
    }
}
