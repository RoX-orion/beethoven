package org.beethoven.pojo.vo;

import lombok.Data;
import org.beethoven.pojo.enums.PlayMode;

import java.util.List;

@Data
public class PlayQueueVo {

    private String id;

    private String userId;

    private String sourceType;

    private String sourceId;

    private PlayMode playMode;

    private String currentMusicId;

    private Integer currentIndex;

    private Integer currentTime;

    private Long randomSeed;

    private Long version;

    private List<PlayQueueItemVo> items;
}
