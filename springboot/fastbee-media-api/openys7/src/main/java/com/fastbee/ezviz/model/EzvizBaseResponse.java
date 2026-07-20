package com.fastbee.ezviz.model;

import lombok.Data;

/**
 * 萤石云 API 统一响应基类
 *
 * @author fastbee
 */
@Data
public class EzvizBaseResponse {

    /** 响应码，200 表示成功 */
    private String code;

    /** 响应消息 */
    private String msg;

    /**
     * 是否请求成功
     */
    public boolean isSuccess() {
        return "200".equals(code);
    }

}
