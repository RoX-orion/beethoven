package org.andre.beethoven;

import jakarta.annotation.Resource;
import org.beethoven.BeethovenApplication;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BeethovenApplication.class)
public class MusicTest {

    @Resource
    private MusicService musicService;

    @Test
    public void upload() {
//        musicService.uploadMusic();
    }

    @Test
    public void fetchMusicFromOss() {
//        musicService.fetchMusicFromOss("http://sl3btfsle.hb-bkt.clouddn.com/rain.mp3");
        musicService.fetchMusicFromOss("http://sl3btfsle.hb-bkt.clouddn.com/rain.mp3");
    }

    @Test
    public void getOssFileInfo() {
        musicService.getOssFileInfo("rain.mp3");
    }
}
