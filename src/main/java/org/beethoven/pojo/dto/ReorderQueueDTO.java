package org.beethoven.pojo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ReorderQueueDTO {

    @NotEmpty
    private List<String> queueItemIds;
}
