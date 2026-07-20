package com.fastbee.iot.model.dashBoard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author gsb
 * @date 2023/11/14 11:57
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Source {

    private String product;

    private String data1;

    private String data2;
}
