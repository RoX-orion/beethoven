package org.beethoven.pojo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.beethoven.pojo.enums.PlayMode;

import java.util.List;

@Data
public class BuildQueueFromMusicListDTO {

    @NotEmpty
    private List<String> musicIds;

    private String startMusicId;

    private PlayMode playMode;

    private String sourceType;

    private String sourceId;
}
