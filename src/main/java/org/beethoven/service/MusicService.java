package org.beethoven.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.beethoven.lib.BeethovenLib;
import org.beethoven.lib.Constant;
import org.beethoven.lib.GlobalConfig;
import org.beethoven.lib.exception.BeethovenException;
import org.beethoven.lib.exception.MediaException;
import org.beethoven.lib.store.StorageContext;
import org.beethoven.lib.store.StorageResponse;
import org.beethoven.mapper.MusicMapper;
import org.beethoven.mapper.MusicPlaylistMapper;
import org.beethoven.pojo.PageInfo;
import org.beethoven.pojo.dto.MusicDTO;
import org.beethoven.pojo.dto.UploadMusicDTO;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.entity.Music;
import org.beethoven.pojo.entity.MusicPlaylist;
import org.beethoven.pojo.enums.StorageProvider;
import org.beethoven.pojo.vo.ManageMusic;
import org.beethoven.pojo.vo.MusicVo;
import org.beethoven.util.FileUtil;
import org.beethoven.util.Helpers;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

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
    private OkHttpClient httpClient;

    @Resource
    private MusicMapper musicMapper;

    @Resource
    private MusicPlaylistMapper musicPlaylistMapper;

    @Resource
    private StorageContext storageContext;

    @Transactional
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
        music.setStorage(StorageProvider.MINIO);
        music.setShardingSize(GlobalConfig.shardingSize);
        musicMapper.insert(music);

        int i;
        byte[] buffer = new byte[4096];
        String fileName = Constant.USER_DIR + ossMusicName;
        File tempFile = new File(fileName);
        if (!tempFile.exists()) {
            try {
                File parentFile = tempFile.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                tempFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        BufferedInputStream bufferedInputStream = null;
        try(InputStream musicInputStream = musicFile.getInputStream();
            InputStream coverInputStream = coverFile.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(fileName)) {
            while ((i = musicInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, i);
            }
            int duration = (int) BeethovenLib.INSTANCE.get_duration(fileName);
            if (duration <= 0) {
                log.error("parse music info error, file name: {}", musicFile.getOriginalFilename());
                throw new MediaException("parse music info error!");
            }
            music.setDuration(duration);

            bufferedInputStream = new BufferedInputStream(new FileInputStream(fileName));
            StorageResponse uploadMusicResponse = storageContext.upload(bufferedInputStream, ossMusicName);
            if (uploadMusicResponse.isOk) {
                music.setHash(uploadMusicResponse.hash);
                music.setOssMusicName(ossMusicName);
            }

            StorageResponse uploadCoverResponse = storageContext.upload(coverInputStream, ossCoverName);
            if (uploadCoverResponse.isOk) {
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
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return ApiResult.ok();
    }

    public void fetchMusic(HttpServletRequest request, HttpServletResponse response, String fileName) {
        String range = request.getHeader("Range");
        Long start = null, end = null;
        try {
            if (StringUtils.hasText(range)) {
                int i = range.indexOf('=');
                if (i != -1) {
                    String byteData = range.substring(i + 1);
                    int splitIndex = 0;
                    if (byteData.charAt(0) != '-') {
                        splitIndex = byteData.indexOf('-');
                        start = Long.valueOf(byteData.substring(0, splitIndex));
                    }
                    if (byteData.length() - 1 != splitIndex) {
                        end = Long.valueOf(byteData.substring(splitIndex + 1));
                    }
                }
            }
        } catch (Exception e) {
            throw new BeethovenException("Invalid split data!");
        }

        try (InputStream inputStream = storageContext.download(fileName, start, end)) {
            byte[] bytes = new byte[4096];
            int len;
            ServletOutputStream outputStream = response.getOutputStream();
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void fetchMusic(String fileName) {
        /*
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
        */
    }

    public void getOssFileInfo(String key) {
//        try {
//            FileInfo fileInfo = bucketManager.stat(bucket, key);
//            System.out.println(fileInfo.hash);
//            System.out.println(fileInfo.fsize);
//            System.out.println(fileInfo.mimeType);
//            System.out.println(fileInfo.putTime);
//        } catch (QiniuException e) {
//            System.out.println(e.response.getInfo().contains("no such file or directory"));
//        }
    }

    public List<MusicVo> searchMusic(MusicDTO musicDTO) {
        int offset = (musicDTO.getPage() - 1) * musicDTO.getSize();
        return musicMapper.searchMusic(offset, musicDTO.getSize(), Helpers.buildFuzzySearchParam(musicDTO.getKey()));
    }

    public MusicVo getMusicInfo(Long id) {
        Music music = musicMapper.selectById(id);
        if (music == null) {
            throw new BeethovenException("音乐不存在！");
        }
        MusicVo musicVo = new MusicVo();
        BeanUtils.copyProperties(music, musicVo);
        musicVo.link = music.getOssMusicName();
        musicVo.cover = music.getOssCoverName();
        return musicVo;
    }

    public PageInfo<List<ManageMusic>> getManageMusicList(@Valid MusicDTO musicDTO) {
        int offset = (musicDTO.getPage() - 1) * musicDTO.getSize();
        String param = Helpers.buildFuzzySearchParam(musicDTO.getKey());
        List<ManageMusic> manageMusicList = musicMapper.getManageMusicList(offset, musicDTO.getSize(), param);
        LambdaQueryWrapper<Music> queryWrapper = new QueryWrapper<Music>().lambda();
        if (StringUtils.hasText(param)) {
            queryWrapper = new QueryWrapper<Music>().lambda().like(Music::getName, param).or().like(Music::getSinger, param);
        }
        Long total = musicMapper.selectCount(queryWrapper);
        return PageInfo.result(manageMusicList, total);
    }

    @Transactional
    public ApiResult<String> deleteMusic(Long musicId) {
        Music music = musicMapper.selectById(musicId);
        if (music == null) {
            return ApiResult.fail("Music is not exist!");
        }

        musicMapper.deleteById(musicId);
        musicPlaylistMapper.delete(
                new LambdaQueryWrapper<MusicPlaylist>().eq(MusicPlaylist::getMusicId, musicId)
        );

        storageContext.remove(music.getOssMusicName());
        storageContext.remove(music.getOssMusicName());

        return ApiResult.ok();
    }

    public ApiResult<String> updateMusic(UploadMusicDTO uploadMusicDTO) {
        if (uploadMusicDTO.getMusicId() == null)
            return ApiResult.fail("Music id can't be null!");
        if (!StringUtils.hasText(uploadMusicDTO.getName()) || !StringUtils.hasText(uploadMusicDTO.getSinger()))
            return ApiResult.fail("Music name or Singer can't be null!");
        Music music = musicMapper.selectById(uploadMusicDTO.getMusicId());
        if (music == null)
            return ApiResult.fail("Music is not exist!");
        /*
        music.setSize(musicFile.getSize());
        music.setMime(musicMime);
        music.setStorage(StorageProvider.MINIO);
        music.setShardingSize(GlobalConfig.shardingSize);
        musicMapper.insert(music);

        int i;
        byte[] buffer = new byte[4096];
        String fileName = Constant.USER_DIR + ossMusicName;
        File tempFile = new File(fileName);
        if (!tempFile.exists()) {
            try {
                File parentFile = tempFile.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                tempFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        BufferedInputStream bufferedInputStream = null;
        try(InputStream musicInputStream = musicFile.getInputStream();
            InputStream coverInputStream = coverFile.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(fileName)) {
            while ((i = musicInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, i);
            }
            int duration = (int) BeethovenLib.INSTANCE.get_duration(fileName);
            if (duration <= 0) {
                log.error("parse music info error, file name: {}", musicFile.getOriginalFilename());
                throw new MediaException("parse music info error!");
            }
            music.setDuration(duration);

            bufferedInputStream = new BufferedInputStream(new FileInputStream(fileName));
            StorageResponse uploadMusicResponse = storageContext.upload(bufferedInputStream, ossMusicName);
            if (uploadMusicResponse.isOk) {
                music.setHash(uploadMusicResponse.hash);
                music.setOssMusicName(ossMusicName);
            }

            StorageResponse uploadCoverResponse = storageContext.upload(coverInputStream, ossCoverName);
            if (uploadCoverResponse.isOk) {
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
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
         */
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
        if (StringUtils.hasText(uploadMusicDTO.getAlbum()))
            music.setAlbum(uploadMusicDTO.getAlbum().trim());
        music.setName(uploadMusicDTO.getName().trim());
        music.setSinger(uploadMusicDTO.getSinger().trim());

        return ApiResult.ok();
    }
}
