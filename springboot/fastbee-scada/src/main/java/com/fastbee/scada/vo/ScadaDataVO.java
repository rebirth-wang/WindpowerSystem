package com.fastbee.scada.vo;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author admin
 * @version 1.0
 * @description: 组态数据VO类
 * @date 2024-08-30 16:09
 */
@Data
public class ScadaDataVO {

    private Integer zIndexTop;

    private Integer zIndexBottom;

    @JSONField(name = "WebTopoGuid")
    private String webToPoGuid;

    @JSONField(name = "WebTopoDeviceImei")
    private String webToPoDeviceImei;

    private List<DataVO> pcConfig;

    private List<DataVO> mdConfig;

    @Data
    public static class DataVO {

        private String name;

        private JSONObject layer;

        private List<JSONObject> components;

    }
}
