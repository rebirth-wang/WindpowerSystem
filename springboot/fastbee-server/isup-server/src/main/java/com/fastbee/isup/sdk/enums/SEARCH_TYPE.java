package com.fastbee.isup.sdk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SEARCH_TYPE {
    ENUM_SEARCH_TYPE_ERR(1, "错误类型"),
    ENUM_SEARCH_RECORD_FILE(0, "视频文件"),
    ENUM_SEARCH_PICTURE_FILE(1, "图片文件"),
    ENUM_SEARCH_FLOW_INFO(2, "流信息"),
    ENUM_SEARCH_DEV_LOG(3, "日志文件"),
    ENUM_SEARCH_ALARM_HOST_LOG(4, "报警主机的日志文件");

    private final Integer code;

    private final String description;
}
