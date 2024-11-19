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
import org.beethoven.lib.BeethovenLib;
import org.beethoven.lib.Constant;
import org.beethoven.lib.GlobalConfig;
import org.beethoven.lib.exception.MediaException;
import org.beethoven.mapper.MusicMapper;
import org.beethoven.pojo.dto.MusicDTO;
import org.beethoven.pojo.dto.UploadMusicDTO;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.entity.Music;
import org.beethoven.pojo.enums.OssProvider;
import org.beethoven.pojo.vo.MusicVo;
import org.beethoven.util.FileUtil;
import org.beethoven.util.Helpers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
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

    public ApiResult<String> uploadMusic(UploadMusicDTO uploadMusicDTO) {
        MultipartFile musicFile = uploadMusicDTO.getMusic();
        MultipartFile coverFile = uploadMusicDTO.getCover();
        String musicMime = musicFile.getContentType();
        if (!FileUtil.checkAudioMime(musicMime)) {
            return ApiResult.fail(String.format("music file content type[%s] not support!", musicMime));
        }
        String coverMime = coverFile.getContentType();
        if (!FileUtil.checkImageMime(coverMime)) {
            return ApiResult.fail(String.format("cover file content type[%s] not support!", coverMime));
        }
        String ossMusicName = Constant.MUSIC_DIR + Helpers.buildOssFileName(musicFile.getOriginalFilename());
        String ossCoverName = Constant.COVER_DIR + Helpers.buildOssFileName(coverFile.getOriginalFilename());
        Music music = new Music();
        music.setName(uploadMusicDTO.getName().trim());
        music.setAlbum(uploadMusicDTO.getAlbum().trim());
        music.setSinger(uploadMusicDTO.getSinger().trim());
        music.setSize(musicFile.getSize());
        music.setMime(musicMime);
        music.setOss(OssProvider.QINIU);
        music.setShardingSize(GlobalConfig.shardingSize);
        musicMapper.insert(music);

        String token = auth.uploadToken(bucket);
        int i;
        byte[] buffer = new byte[4096];
        String fileName = Constant.USER_DIR + "/musicTemp/" + musicFile.getOriginalFilename();
        try(InputStream musicInputStream = musicFile.getInputStream();
            InputStream coverInputStream = coverFile.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(fileName)) {

            while ((i = musicInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, i);
            }
            int duration = BeethovenLib.INSTANCE.get_duration(fileName);
            if (duration <= 0) {
                log.error("parse music info error, file name: {}", musicFile.getOriginalFilename());
                throw new MediaException("parse music info error!");
            }
            music.setDuration(duration);
            com.qiniu.http.Response uploadMusicResponse = uploadManager.put(musicInputStream, ossMusicName, token, null, null);
            if (uploadMusicResponse.isOK()) {
                music.setHash((String) uploadMusicResponse.jsonToMap().get("hash"));
                music.setOssMusicName(ossMusicName);
            }

            com.qiniu.http.Response uploadCoverResponse = uploadManager.put(coverInputStream, ossCoverName, token, null, null);
            if (uploadCoverResponse.isOK()) {
                music.setOssCoverName(ossCoverName);
            }
            musicMapper.updateById(music);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
        }

        return ApiResult.ok();
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
