package org.beethoven.service;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.beethoven.mapper.MusicMapper;
import org.beethoven.pojo.dto.MusicDTO;
import org.beethoven.pojo.vo.MusicVo;
import org.beethoven.util.Helpers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;

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

    @Resource
    private Auth auth;

    @Resource
    private OkHttpClient httpClient;

    @Resource
    private UploadManager uploadManager;

    @Resource
    private BucketManager bucketManager;

    @Resource
    private MusicMapper musicMapper;

    @Value("${oss.qiniu.bucket}")
    private String bucket;

    @Transactional(rollbackFor = Exception.class)
    public String uploadMusic(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        try {
            InputStream inputStream = file.getInputStream();
            String token = auth.uploadToken(bucket);
            com.qiniu.http.Response response = uploadManager.put(inputStream, Helpers.buildOssFileName(fileName), token, null, null);
        } catch (QiniuException e) {
            log.error("File upload fail");
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("");
            throw new RuntimeException(e);
        }

//        long length = file.length();
//        System.out.println(length);
//            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
//
//            System.out.println(response.getInfo());
        //        catch (QiniuException e) {
//            throw new RuntimeException(e);
//        }
        return "";
    }

    public void fetchMusic(HttpServletRequest request, String hash) {
        String range = request.getHeader("Range");
        if (StringUtils.hasText(range)) {

        } else {

        }
        System.out.println(range);
    }

    public void fetchMusicFromOss(String url) {
//        long expireInSeconds = 10;//1小时，可以自定义链接过期时间
//        String finalUrl = auth.privateDownloadUrl(url, expireInSeconds);
//        System.out.println(finalUrl);
        Headers headers = Headers.of(Map.of("Range", "bytes=0-1100"));
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .get()
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            // 获取结果
            ResponseBody body = response.body();
            byte[] bytes;
            if (body != null) {
                bytes = body.bytes();
                System.out.println(bytes.length);
            }
//            log.info("请求地址:{}，请求结果:{}", url，result);
//            return Boolean.parseBoolean(result);
        } catch (SocketTimeoutException e) {
            log.error("签名验证请求超时异常，请求地址:{}", url);
//            return false;
        } catch (Exception e) {
            log.error("签名验证失败出现异常，错误信息: {}", e.getMessage());
//            return false;
        }
    }

    public void getOssFileInfo(String key) {
        try {
            FileInfo fileInfo = bucketManager.stat(bucket, key);
            System.out.println(fileInfo.hash);
            System.out.println(fileInfo.fsize);
            System.out.println(fileInfo.mimeType);
            System.out.println(fileInfo.putTime);
        } catch (QiniuException e) {
            System.out.println(e.response.getInfo().contains("no such file or directory"));
        }
    }

    public List<MusicVo> searchMusic(MusicDTO musicDTO) {
        int offset = (musicDTO.getPage() - 1) * musicDTO.getSize();
        return musicMapper.searchMusic(offset, musicDTO.getSize(), Helpers.buildFuzzySearchParam(musicDTO.getKey()));
    }
}
