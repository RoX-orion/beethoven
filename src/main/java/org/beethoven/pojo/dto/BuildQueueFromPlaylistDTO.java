package org.beethoven.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.beethoven.pojo.enums.PlayMode;

@Data
public class BuildQueueFromPlaylistDTO {

    @NotBlank
    private String playlistId;

    private String startMusicId;

    private PlayMode playMode;
}
