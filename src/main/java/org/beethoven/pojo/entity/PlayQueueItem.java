package org.beethoven.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("play_queue_item")
public class PlayQueueItem {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String queueId;

    private String musicId;

    private Integer sortNo;

    private String sourceType;

    private String sourceId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
