package com.fastbee.iot.model.ThingsModels;

import java.util.Date;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * redis缓存物模型值
 * @author gsb
 * @date 2023/6/3 13:48
 */
@Data
@NoArgsConstructor
public class ValueItem {

    /**
     * 标识符
     */
    private String id;

    /**
     * 物模型值
     */
    private String value;

    /**
     * 影子值
     **/
    private String shadow;

    /**
     * 物模型名称
     **/
    private String name;

    /**
     * 上报时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date ts;

    private String remark;

}
