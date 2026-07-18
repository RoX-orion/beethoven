package org.beethoven.pojo.dto;

import lombok.Data;

@Data
public class UpdateCurrentQueueDTO {

    private String musicId;

    private Integer currentIndex;

    private Integer currentTime;
}
