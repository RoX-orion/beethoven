package org.andre.beethoven;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import jakarta.annotation.Resource;
import org.beethoven.BeethovenApplication;
import org.beethoven.mapper.MusicMapper;
import org.beethoven.pojo.entity.Music;
import org.beethoven.pojo.enums.OssProvider;
import org.beethoven.service.MusicService;
import org.beethoven.util.Helpers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

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

    @Resource
    private UploadManager uploadManager;

    @Resource
    private Auth auth;

    @Value("${oss.qiniu.bucket}")
    private String bucket;

    @Resource
    private ObjectMapper mapper;

    @Resource
    private MusicMapper musicMapper;

    @Test
    public void upload() throws IOException {
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream("C:\\Users\\52828\\Desktop\\io.png"));
        String fileName = "io.png";
        String token = auth.uploadToken(bucket);
        com.qiniu.http.Response response = uploadManager.put(inputStream, "video/" + Helpers.buildOssFileName(fileName), token, null, null);
        System.out.println(response.isOK());
        System.out.println(response.jsonToMap().get("hash"));
//        Map<String, String> map = mapper.readValue(response.getInfo(), new TypeReference<HashMap<String, String>>() {});
//        System.out.println(map.get("hash"));
//        System.out.println(response.getResponse().message());
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

    @Test
    public void insertMusic() {
        Music music = new Music();
        music.setName("test");
        music.setAlbum("album");
        music.setSinger("lizhi");
        music.setSize(123456);
        music.setMime("abc");
        music.setOss(OssProvider.QINIU);
        musicMapper.insert(music);
    }
}
