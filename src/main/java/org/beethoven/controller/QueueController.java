package org.beethoven.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.enums.PlayMode;
import org.beethoven.pojo.vo.MusicInfo;
import org.beethoven.service.QueueService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("queue")
public class QueueController {

    @Resource
    private QueueService queueService;

    @RequestMapping(method = RequestMethod.GET)
    public ApiResult<List<MusicInfo>> getQueue(@RequestParam("playlistId") Long playlistId,
                                    @RequestParam("playModel") @Valid @NotNull PlayMode playModel) {
        List<MusicInfo> musicInfoList = queueService.getQueue(playlistId, playModel);

        return ApiResult.ok(musicInfoList);
    }
}
