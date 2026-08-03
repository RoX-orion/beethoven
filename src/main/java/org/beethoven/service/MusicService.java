package org.beethoven.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.beethoven.lib.AuthContext;
import org.beethoven.lib.BeethovenLib;
import org.beethoven.lib.Constant;
import org.beethoven.lib.GlobalConfig;
import org.beethoven.lib.exception.BeethovenException;
import org.beethoven.lib.exception.MediaException;
import org.beethoven.lib.store.StorageContext;
import org.beethoven.lib.store.StorageResponse;
import org.beethoven.mapper.FileInfoMapper;
import org.beethoven.mapper.MusicMapper;
import org.beethoven.mapper.MusicPlaylistMapper;
import org.beethoven.mapper.VideoMapper;
import org.beethoven.pojo.PageInfo;
import org.beethoven.pojo.dto.SearchDTO;
import org.beethoven.pojo.dto.UpdateMusicDTO;
import org.beethoven.pojo.dto.UploadMusicDTO;
import org.beethoven.pojo.entity.*;
import org.beethoven.pojo.vo.MusicInfo;
import org.beethoven.pojo.vo.MusicManagement;
import org.beethoven.util.FileUtil;
import org.beethoven.util.Helpers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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
    private MusicMapper musicMapper;

    @Resource
    private MusicPlaylistMapper musicPlaylistMapper;

    @Resource
    private StorageContext storageContext;

    @Resource
    private FileInfoMapper fileInfoMapper;

    @Resource
    private VideoMapper videoMapper;
    @Autowired
    private AuthContext authContext;

    @Transactional(rollbackFor = Exception.class)
    public ApiResult<String> uploadMusic(UploadMusicDTO uploadMusicDTO) throws IOException {
        MultipartFile musicFile = uploadMusicDTO.getMusic();
        MultipartFile coverFile = uploadMusicDTO.getCover();
        String musicMime = musicFile.getContentType();
        if (!FileUtil.checkMime(musicMime, FileUtil.FileType.AUDIO)) {
            return ApiResult.fail(String.format("music file content type[%s] not support!", musicMime));
        }
        String coverMime = coverFile.getContentType();
        if (!FileUtil.checkMime(coverMime, FileUtil.FileType.IMAGE)) {
            return ApiResult.fail(String.format("cover file content type[%s] not support!", coverMime));
        }
        String ossMusicName = Constant.MUSIC_DIR + Helpers.buildOssFileName(musicFile.getOriginalFilename());
        String ossCoverName = Constant.COVER_DIR + Helpers.buildOssFileName(coverFile.getOriginalFilename());
        Music music = new Music();
        music.setName(uploadMusicDTO.getName().trim());
        music.setAlbum(StringUtils.hasText(uploadMusicDTO.getAlbum()) ? uploadMusicDTO.getAlbum().trim() : null);
        music.setSinger(uploadMusicDTO.getSinger().trim());
        music.setShardingSize(GlobalConfig.shardingSize);
        musicMapper.insert(music);

        int i;
        byte[] buffer = new byte[4096];
        String fileName = Constant.USER_DIR + "/" + ossMusicName;
        File tempFile = new File(fileName);
        if (!tempFile.exists()) {
            File parentFile = tempFile.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            tempFile.createNewFile();
        }

        try(InputStream musicInputStream = musicFile.getInputStream();
            InputStream coverInputStream = coverFile.getInputStream()) {
            // 先将上传文件写入临时文件
            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                while ((i = musicInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, i);
                }
                outputStream.flush();
            }
            int duration = (int) BeethovenLib.INSTANCE.get_duration(fileName);
            if (duration <= 0) {
                log.error("parse music info error, file name: {}", musicFile.getOriginalFilename());
                throw new MediaException("parse music info error!");
            }
            music.setDuration(duration);

            FileInfo musicFileInfo = new FileInfo();
            musicFileInfo.setOriginalFilename(musicFile.getOriginalFilename());
            musicFileInfo.setFilename(ossMusicName);
            musicFileInfo.setSize(musicFile.getSize());
            musicFileInfo.setMime(musicMime);
            musicFileInfo.setChecksum(Files.asByteSource(new File(fileName)).hash(Hashing.sha256()).toString());
            musicFileInfo.setStorage(storageContext.getProvider());

            FileInfo coverFileInfo = new FileInfo();
            coverFileInfo.setOriginalFilename(coverFile.getOriginalFilename());
            coverFileInfo.setFilename(ossCoverName);
            coverFileInfo.setSize(coverFile.getSize());
            coverFileInfo.setMime(coverMime);
            coverFileInfo.setChecksum("");
            coverFileInfo.setStorage(storageContext.getProvider());

            fileInfoMapper.insert(musicFileInfo);
            fileInfoMapper.insert(coverFileInfo);

            music.setMusicFileId(musicFileInfo.getId());
            music.setCoverFileId(coverFileInfo.getId());

            // 文件写入完成后，再打开流上传到存储
            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileName))) {
                StorageResponse uploadMusicResponse = storageContext.upload(
                        bufferedInputStream,
                        ossMusicName,
                        musicFile.getSize()
                );
                if (uploadMusicResponse == null || !uploadMusicResponse.isOk()) {
                    log.error("upload music file failed: {}", ossMusicName);
                    throw new BeethovenException("Upload music file failed!");
                }
                musicFileInfo.setHash(uploadMusicResponse.getHash());
            }
            fileInfoMapper.updateById(musicFileInfo);

            StorageResponse uploadCoverResponse = storageContext.upload(
                    coverInputStream,
                    ossCoverName,
                    coverFile.getSize()
            );
            if (uploadCoverResponse == null || !uploadCoverResponse.isOk()) {
                log.error("upload cover file failed: {}", ossCoverName);
                throw new BeethovenException("Upload cover file failed!");
            }
            coverFileInfo.setHash(uploadCoverResponse.getHash());
            fileInfoMapper.updateById(coverFileInfo);
            musicMapper.updateById(music);
        } finally {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
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

    public List<MusicInfo> searchMusic(SearchDTO searchDTO) {
        int offset = (searchDTO.getPage() - 1) * searchDTO.getSize();
        return musicMapper.searchMusic(offset, searchDTO.getSize(), Helpers.buildFuzzySearchParam(searchDTO.getKey()));
    }

    public MusicInfo getMusicInfo(String id) {
        Music music = musicMapper.selectById(id);
        if (music == null) {
            throw new BeethovenException("音乐不存在！");
        }
        MusicInfo musicInfo = new MusicInfo();
        BeanUtils.copyProperties(music, musicInfo);
        FileInfo musicFileInfo = fileInfoMapper.selectById(music.getMusicFileId());
        if (musicFileInfo != null)
            musicInfo.link = musicFileInfo.getFilename();
        FileInfo coverFileInfo = fileInfoMapper.selectById(music.getCoverFileId());
        if (coverFileInfo != null)
            musicInfo.cover = coverFileInfo.getFilename();
        return musicInfo;
    }

    public PageInfo<MusicManagement> getManageMusicList(@Valid SearchDTO searchDTO) {
        int offset = (searchDTO.getPage() - 1) * searchDTO.getSize();
        String key = Helpers.buildFuzzySearchParam(searchDTO.getKey());
        List<MusicManagement> musicManagementList = musicMapper.getManageMusicList(offset, searchDTO.getSize(), key);
        LambdaQueryWrapper<Music> queryWrapper = new QueryWrapper<Music>().lambda();
        if (StringUtils.hasText(key)) {
            queryWrapper = new QueryWrapper<Music>().lambda().like(Music::getName, key).or().like(Music::getSinger, key);
        }
        Long total = musicMapper.selectCount(queryWrapper);
        return PageInfo.result(musicManagementList, total);
    }

    @Transactional
    public ApiResult<String> deleteMusic(String musicId) {
        Music music = musicMapper.selectById(musicId);
        if (music == null) {
            return ApiResult.fail("Music is not exist!");
        }

        // 先查询文件信息，再删除数据库记录，最后清理存储文件
        List<String> fileIds = List.of(music.getMusicFileId(), music.getCoverFileId());
        List<FileInfo> fileInfoList = fileInfoMapper.selectBatchIds(fileIds);

        musicMapper.deleteById(musicId);
        musicPlaylistMapper.delete(
                new LambdaQueryWrapper<MusicPlaylist>().eq(MusicPlaylist::getMusicId, musicId)
        );
        fileInfoMapper.deleteByIds(fileIds);
        for (FileInfo fileInfo : fileInfoList) {
            removeReplacedFileAfterCommit(fileInfo);
        }

        return ApiResult.ok();
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiResult<String> updateMusic(UpdateMusicDTO updateMusicDTO) throws IOException {
        String userId = authContext.getUserId();
        if (userId == null)
            return ApiResult.expired("Get user info fail!");
        if (updateMusicDTO.getMusicId() == null)
            return ApiResult.fail("Music id can't be null!");
        if (!StringUtils.hasText(updateMusicDTO.getName()) || !StringUtils.hasText(updateMusicDTO.getSinger()))
            return ApiResult.fail("Music name or Singer can't be null!");
        Music music = musicMapper.selectById(updateMusicDTO.getMusicId());
        if (music == null)
            return ApiResult.fail("Music is not exist!");

        MultipartFile musicFile = updateMusicDTO.getMusic();
        MultipartFile coverFile = updateMusicDTO.getCover();
        MultipartFile videoFile = updateMusicDTO.getVideo();
        String musicMime = null;
        String coverMime = null;
        String videoMime = null;
        if (musicFile != null)
            musicMime = musicFile.getContentType();
        if (coverFile != null)
            coverMime = coverFile.getContentType();
        if (videoFile != null)
            videoMime = videoFile.getContentType();
        if (musicFile != null && !FileUtil.checkMime(musicMime, FileUtil.FileType.AUDIO)) {
            return ApiResult.fail(String.format("music file content type[%s] not support!", musicMime));
        }
        if (coverFile != null && !FileUtil.checkMime(coverMime, FileUtil.FileType.IMAGE)) {
            return ApiResult.fail(String.format("cover file content type[%s] not support!", coverMime));
        }
        if (videoFile != null && !FileUtil.checkMime(videoMime, FileUtil.FileType.VIDEO)) {
            return ApiResult.fail(String.format("video file content type[%s] not support!", videoMime));
        }
        if (StringUtils.hasText(updateMusicDTO.getAlbum()))
            music.setAlbum(updateMusicDTO.getAlbum().trim());
        music.setName(updateMusicDTO.getName().trim());
        music.setSinger(updateMusicDTO.getSinger().trim());

        if (musicFile != null) {
            String ossMusicName = Constant.MUSIC_DIR + Helpers.buildOssFileName(musicFile.getOriginalFilename());
            String fileName = Constant.USER_DIR + "/" + ossMusicName;
            File tempFile = new File(fileName);
            if (!tempFile.exists()) {
                File parentFile = tempFile.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                tempFile.createNewFile();
            }

            int i;
            byte[] buffer = new byte[4096];
            try(InputStream musicInputStream = musicFile.getInputStream()) {
                // 先将上传文件写入临时文件
                try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                    while ((i = musicInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, i);
                    }
                    outputStream.flush();
                }
                int duration = (int) BeethovenLib.INSTANCE.get_duration(fileName);
                if (duration <= 0) {
                    log.error("parse music info error, file name: {}", musicFile.getOriginalFilename());
                    throw new MediaException("parse music info error!");
                }
                music.setDuration(duration);

                FileInfo musicFileInfo = new FileInfo();
                musicFileInfo.setOriginalFilename(musicFile.getOriginalFilename());
                musicFileInfo.setFilename(ossMusicName);
                musicFileInfo.setSize(musicFile.getSize());
                musicFileInfo.setMime(musicMime);
                musicFileInfo.setChecksum(Files.asByteSource(new File(fileName)).hash(Hashing.sha256()).toString());
                musicFileInfo.setStorage(storageContext.getProvider());
                fileInfoMapper.insert(musicFileInfo);

                // 文件写入完成后，再打开流上传到存储
                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileName))) {
                StorageResponse uploadMusicResponse = storageContext.upload(
                        bufferedInputStream,
                        ossMusicName,
                        musicFile.getSize()
                );
                if (uploadMusicResponse == null || !uploadMusicResponse.isOk()) {
                    log.error("upload music file failed: {}", ossMusicName);
                    throw new BeethovenException("Upload music file failed!");
                }
                musicFileInfo.setHash(uploadMusicResponse.getHash());
                }
                FileInfo oldMusicFileInfo = fileInfoMapper.selectById(music.getMusicFileId());
                music.setMusicFileId(musicFileInfo.getId());
                fileInfoMapper.updateById(musicFileInfo);
                removeReplacedFileAfterCommit(oldMusicFileInfo);
            } catch (IOException e) {
                throw new BeethovenException("Upload music file error: " + e.getMessage());
            } finally {
                File file = new File(fileName);
                if (file.exists()) {
                    file.delete();
                }
            }
        }

        if (videoFile != null) {
            Video video = null;
            if (music.getVideoId() != null)
                video = videoMapper.selectById(music.getVideoId());
            if (video == null) {
                video = new Video();
                video.setCreator(userId);
            }
            video.setUpdater(userId);
            String ossVideoName = Constant.VIDEO_DIR + Helpers.buildOssFileName(videoFile.getOriginalFilename());
            String fileName = Constant.USER_DIR + "/" + ossVideoName;
            File tempFile = new File(fileName);
            if (!tempFile.exists()) {
                File parentFile = tempFile.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                tempFile.createNewFile();
            }

            int i;
            byte[] buffer = new byte[4096];
            try(InputStream videoInputStream = videoFile.getInputStream()) {
                // 先将上传文件写入临时文件
                try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                    while ((i = videoInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, i);
                    }
                    outputStream.flush();
                }
                int duration = (int) BeethovenLib.INSTANCE.get_duration(fileName);
                if (duration <= 0) {
                    log.error("parse video info error, file name: {}", videoFile.getOriginalFilename());
                    throw new MediaException("parse video info error!");
                }

                FileInfo oldVideoFileInfo = video.getVideoFileId() == null
                        ? null
                        : fileInfoMapper.selectById(video.getVideoFileId());
                FileInfo videoFileInfo = new FileInfo();
                videoFileInfo.setOriginalFilename(videoFile.getOriginalFilename());
                videoFileInfo.setFilename(ossVideoName);
                videoFileInfo.setSize(videoFile.getSize());
                videoFileInfo.setMime(videoMime);
                videoFileInfo.setChecksum(Files.asByteSource(new File(fileName)).hash(Hashing.sha256()).toString());
                videoFileInfo.setStorage(storageContext.getProvider());

                video.setDuration(duration);

                // 文件写入完成后，再打开流上传到存储
                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileName))) {
                    StorageResponse uploadVideoResponse = storageContext.upload(
                            bufferedInputStream,
                            ossVideoName,
                            videoFile.getSize()
                    );
                    if (uploadVideoResponse == null || !uploadVideoResponse.isOk()) {
                        log.error("upload video file failed: {}", ossVideoName);
                        throw new BeethovenException("Upload video file failed!");
                    }
                    videoFileInfo.setHash(uploadVideoResponse.getHash());
                }
                fileInfoMapper.insert(videoFileInfo);
                video.setVideoFileId(videoFileInfo.getId());
                if (video.getId() == null) {
                    videoMapper.insert(video);
                } else {
                    videoMapper.updateById(video);
                }
                music.setVideoId(video.getId());
                removeReplacedFileAfterCommit(oldVideoFileInfo);
            } catch (IOException e) {
                throw new BeethovenException("Upload video file error: " + e.getMessage());
            } finally {
                File file = new File(fileName);
                if (file.exists()) {
                    file.delete();
                }
            }
        }

        if (coverFile != null) {
            String ossCoverName = Constant.COVER_DIR + Helpers.buildOssFileName(coverFile.getOriginalFilename());
            FileInfo oldCoverFileInfo = music.getCoverFileId() == null
                    ? null
                    : fileInfoMapper.selectById(music.getCoverFileId());
            try(InputStream coverInputStream = coverFile.getInputStream()) {
                FileInfo coverFileInfo = new FileInfo();
                coverFileInfo.setOriginalFilename(coverFile.getOriginalFilename());
                coverFileInfo.setFilename(ossCoverName);
                coverFileInfo.setSize(coverFile.getSize());
                coverFileInfo.setMime(coverMime);
                coverFileInfo.setChecksum("");
                coverFileInfo.setStorage(storageContext.getProvider());

                StorageResponse uploadCoverResponse = storageContext.upload(
                        coverInputStream,
                        ossCoverName,
                        coverFile.getSize()
                );
                if (uploadCoverResponse == null || !uploadCoverResponse.isOk()) {
                    throw new BeethovenException("Upload cover file failed!");
                }
                coverFileInfo.setHash(uploadCoverResponse.getHash());
                fileInfoMapper.insert(coverFileInfo);
                music.setCoverFileId(coverFileInfo.getId());
                removeReplacedFileAfterCommit(oldCoverFileInfo);
            }
        }

        musicMapper.updateById(music);

        return ApiResult.ok();
    }

    private void removeReplacedFileAfterCommit(FileInfo oldFileInfo) {
        if (oldFileInfo == null) {
            return;
        }
        fileInfoMapper.deleteById(oldFileInfo.getId());
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    removeStorageObject(oldFileInfo);
                }
            });
            return;
        }
        removeStorageObject(oldFileInfo);
    }

    private void removeStorageObject(FileInfo oldFileInfo) {
        try {
            storageContext.remove(oldFileInfo.getFilename());
        } catch (RuntimeException e) {
            log.warn("Failed to remove replaced media file {}", oldFileInfo.getFilename(), e);
        }
    }
}
