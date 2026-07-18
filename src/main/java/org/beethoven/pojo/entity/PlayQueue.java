package org.beethoven.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.beethoven.pojo.enums.PlayMode;

import java.time.LocalDateTime;

@Data
@TableName("play_queue")
public class PlayQueue {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String userId;

    private String sourceType;

    private String sourceId;

    private PlayMode playMode;

    private String currentMusicId;

    private Integer currentIndex;

    @TableField("play_time")
    private Integer currentTime;

    private Long randomSeed;

    private Long version;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
