package org.beethoven.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddQueueItemDTO {

    @NotBlank
    private String musicId;

    private String sourceType;

    private String sourceId;
}
