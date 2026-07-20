package com.fastbee.media.model;

import lombok.Data;

@Data
public class PtzInput {
    Integer leftRight;
    Integer upDown;
    Integer inOut;
    Integer moveSpeed;
}
