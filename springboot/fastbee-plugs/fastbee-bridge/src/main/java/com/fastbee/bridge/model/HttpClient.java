package com.fastbee.bridge.model;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.BaseEntity;

/**
 * http桥接配置对象 http_client
 *
 * @author gx_magx_ma
 * @date 2024-05-29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HttpClient extends BaseEntity
{
    /** 请求方法 */
    @Excel(name = "请求方法")
    private String method;

    /** 桥接地址 */
    @Excel(name = "桥接地址")
    private String hostUrl;

    /** 请求头 */
    @Excel(name = "请求头")
    private String requestHeaders;

    /** 请求参数 */
    @Excel(name = "请求参数")
    private String requestQuerys;

    /** 请求参数 */
    @Excel(name = "请求参数")
    private String requestConfig;

    /** 请求体 */
    @Excel(name = "请求体")
    private String requestBody;

    /** 是否获取返回值 */
    @Excel(name = "是否获取返回值")
    private Boolean isGetResponse;

    /** 返回值绑定字段 */
    @Excel(name = "返回值绑定字段")
    private Map<String, String> resValueBind;
}
