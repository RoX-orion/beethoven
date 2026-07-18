package org.beethoven.pojo.vo;

import lombok.Data;

@Data
public class PlayQueueItemVo {

    private String queueItemId;

    private String musicId;

    private MusicInfo music;

    private String sourceType;

    private String sourceId;

    private Integer sortNo;
}
