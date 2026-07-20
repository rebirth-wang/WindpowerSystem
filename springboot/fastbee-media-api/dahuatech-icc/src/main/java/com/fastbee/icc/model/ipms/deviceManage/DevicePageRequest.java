package com.fastbee.icc.model.ipms.deviceManage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @className DevicePageRequest
 * @Author 355079
 * @Date 2024/12/10
 * @Description 设备列表查询接口请求参数
 */
@Data
@Slf4j
public class DevicePageRequest {
    /**
     * 页码
     */
    private Integer pageNum = 1;
    /**
     * 每页条数
     */
    private Integer pageSize = 10;
    /**
     * 设备大类
     */
    private Integer deviceCategory;
    /**
     * 所属组织
     */
    private String ownerCode;
    /**
     * 设备小类
     */
    private Integer deviceType;
    /**
     * 是否在线 1:在线 0：离线
     */
    private Integer isOnline;
    /**
     * 设备ip 支持模糊查询
     */
    private String deviceIp;
    /**
     * 设备编码 支持模糊查询
     */
    private String deviceCode;
    /**
     * 设备名称 支持模糊查询
     */
    private String deviceName;
    /**
     * 关键字（编码/设备/ip地址） 支持模糊查询
     */
    private String searchKey;

    /**
     * 获取URL后缀
     *
     * @return URL后缀
     */
    public String getUrlSuffix() {
        StringBuilder urlSuffix = new StringBuilder();
        String encodeUrlSuffix = "";
        urlSuffix.append("?");
        urlSuffix.append("pageNum=" + pageNum);
        urlSuffix.append("&pageSize=" + pageSize);
        if (deviceCategory != null) {
            urlSuffix.append("&deviceCategory=" + deviceCategory);
        }
        if (!StringUtils.isEmpty(ownerCode)) {
            urlSuffix.append("&ownerCode=" + ownerCode);
        }
        if (deviceType != null) {
            urlSuffix.append("&deviceType=" + deviceType);
        }
        if (isOnline != null) {
            urlSuffix.append("&isOnline=" + isOnline);
        }
        if (!StringUtils.isEmpty(deviceIp)) {
            urlSuffix.append("&deviceIp=" + deviceIp);
        }
        if (!StringUtils.isEmpty(deviceCode)) {
            urlSuffix.append("&deviceCode=" + deviceCode);
        }
        if (!StringUtils.isEmpty(deviceName)) {
            try {
                urlSuffix.append("&deviceName=" + URLEncoder.encode(deviceName, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        if (!StringUtils.isEmpty(searchKey)) {
            try {
                urlSuffix.append("&searchKey=" + URLEncoder.encode(searchKey, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return urlSuffix.toString();
    }
}
