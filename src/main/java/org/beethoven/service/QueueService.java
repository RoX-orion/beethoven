package org.beethoven.service;

import jakarta.annotation.Resource;
import org.beethoven.mapper.PlaylistMapper;
import org.beethoven.pojo.enums.PlayMode;
import org.beethoven.pojo.vo.MusicInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueueService {

    @Resource
    private PlaylistMapper playlistMapper;

    public List<MusicInfo> getQueue(Long playlistId, PlayMode playModel) {
        List<MusicInfo> musicInfoList = null;
        if (playlistId != null) {
            musicInfoList = playlistMapper.getPlaylistMusic(playlistId, null);
        } else {

        }

        return musicInfoList;
    }

//    private void
}
