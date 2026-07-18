package org.beethoven.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.beethoven.lib.annotation.Permission;
import org.beethoven.pojo.dto.*;
import org.beethoven.pojo.entity.ApiResult;
import org.beethoven.pojo.vo.PlayQueueVo;
import org.beethoven.service.QueueService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("queue")
public class QueueController {

    @Resource
    private QueueService queueService;

    @Permission
    @GetMapping
    public ApiResult<PlayQueueVo> getQueue() {
        return ApiResult.ok(queueService.getQueue());
    }

    @Permission
    @PostMapping("fromPlaylist")
    public ApiResult<PlayQueueVo> buildFromPlaylist(@RequestBody @Valid BuildQueueFromPlaylistDTO buildQueueFromPlaylistDTO) {
        return ApiResult.ok(queueService.buildFromPlaylist(buildQueueFromPlaylistDTO));
    }

    @Permission
    @PostMapping("fromMusicList")
    public ApiResult<PlayQueueVo> buildFromMusicList(@RequestBody @Valid BuildQueueFromMusicListDTO buildQueueFromMusicListDTO) {
        return ApiResult.ok(queueService.buildFromMusicList(buildQueueFromMusicListDTO));
    }

    @Permission
    @PostMapping("items")
    public ApiResult<PlayQueueVo> addToEnd(@RequestBody @Valid AddQueueItemDTO addQueueItemDTO) {
        return ApiResult.ok(queueService.addToEnd(addQueueItemDTO));
    }

    @Permission
    @PostMapping("items/next")
    public ApiResult<PlayQueueVo> addToNext(@RequestBody @Valid AddQueueItemDTO addQueueItemDTO) {
        return ApiResult.ok(queueService.addToNext(addQueueItemDTO));
    }

    @Permission
    @PutMapping("reorder")
    public ApiResult<PlayQueueVo> reorder(@RequestBody @Valid ReorderQueueDTO reorderQueueDTO) {
        return ApiResult.ok(queueService.reorder(reorderQueueDTO));
    }

    @Permission
    @PutMapping("current")
    public ApiResult<PlayQueueVo> updateCurrent(@RequestBody UpdateCurrentQueueDTO updateCurrentQueueDTO) {
        return ApiResult.ok(queueService.updateCurrent(updateCurrentQueueDTO));
    }

    @Permission
    @PutMapping("playMode")
    public ApiResult<PlayQueueVo> updatePlayMode(@RequestBody @Valid UpdatePlayModeDTO updatePlayModeDTO) {
        return ApiResult.ok(queueService.updatePlayMode(updatePlayModeDTO.getPlayMode()));
    }

    @Permission
    @DeleteMapping("items/{queueItemId}")
    public ApiResult<PlayQueueVo> removeItem(@PathVariable String queueItemId) {
        return ApiResult.ok(queueService.removeItem(queueItemId));
    }

    @Permission
    @DeleteMapping
    public ApiResult<PlayQueueVo> clear() {
        return ApiResult.ok(queueService.clear());
    }
}
