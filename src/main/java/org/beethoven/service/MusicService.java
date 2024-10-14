package org.beethoven.service;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-10-09
 */

@Slf4j
@Service
public class MusicService {

    private final Auth auth;

    private final UploadManager uploadManager;

    @Value("${oss.qiniu.bucket}")
    private String bucket;

    @Autowired
    public MusicService(
            final Auth auth,
            final UploadManager uploadManager
    ) {
        this.auth = auth;
        this.uploadManager = uploadManager;
    }

    public String uploadResource() {
        try {
            String token = auth.uploadToken(bucket);
            File file = new File("C:\\Users\\52828\\Desktop\\rain.mp3");
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            Response response = uploadManager.put(inputStream, file.getName(), token, null, null);
            System.out.println(response.getInfo());
        } catch (FileNotFoundException e) {
            log.error("");
            throw new RuntimeException(e);
        } catch (QiniuException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

}
