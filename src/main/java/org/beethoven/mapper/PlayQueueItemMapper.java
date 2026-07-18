package org.beethoven.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.beethoven.pojo.entity.PlayQueueItem;
import org.beethoven.pojo.vo.PlayQueueItemVo;

import java.util.List;

public interface PlayQueueItemMapper extends BaseMapper<PlayQueueItem> {

    List<PlayQueueItemVo> getQueueItems(@Param("queueId") String queueId);

    Integer getMaxSortNo(@Param("queueId") String queueId);

    int shiftSortNoFrom(@Param("queueId") String queueId,
                        @Param("sortNo") Integer sortNo);

    int decrementSortNoAfter(@Param("queueId") String queueId,
                             @Param("sortNo") Integer sortNo);
}
