package org.beethoven.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.beethoven.pojo.enums.PlayMode;

@Data
public class UpdatePlayModeDTO {

    @NotNull
    private PlayMode playMode;
}
