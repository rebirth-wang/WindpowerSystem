package com.fastbee.ezviz.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fastbee.ezviz.client.EzvizHttpClient;
import com.fastbee.ezviz.exception.EzvizApiException;
import com.fastbee.ezviz.model.EzvizBaseResponse;
import com.fastbee.ezviz.model.EzvizCameraInfo;
import com.fastbee.ezviz.model.EzvizDeviceInfo;
import com.fastbee.media.enums.ChannelStreamType;

/**
 * 萤石云设备管理服务
 * <p>对接萤石云开放平台设备相关接口</p>
 *
 * @author fastbee
 */
@Slf4j
@Service(ChannelStreamType.DEVICEINFO_SERVICE + ChannelStreamType.EZVIZ)
public class EzvizDeviceService {

    /** 获取设备列表 */
    private static final String API_DEVICE_LIST   = "/api/lapp/device/list";
    /** 获取设备信息 */
    private static final String API_DEVICE_INFO   = "/api/lapp/device/info";
    /** 获取设备摄像头列表 */
    private static final String API_CAMERA_LIST   = "/api/lapp/camera/list";
    /** 修改设备名称 */
    private static final String API_DEVICE_RENAME = "/api/lapp/device/name/update";

    private final EzvizHttpClient httpClient;

    public EzvizDeviceService(EzvizHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * 获取用户下所有设备列表（支持分页）
     *
     * @param pageStart 分页起始页，从 0 开始
     * @param pageSize  每页数量，最大50
     * @return 设备信息列表
     */
    public List<EzvizDeviceInfo> listDevices(int pageStart, int pageSize) {
        Map<String, String> params = new HashMap<>();
        params.put("pageStart", String.valueOf(pageStart));
        params.put("pageSize", String.valueOf(Math.min(pageSize, 50)));
        try {
            EzvizDeviceListResponse resp = httpClient.postWithToken(API_DEVICE_LIST, params, EzvizDeviceListResponse.class);
            return resp.getData() != null ? resp.getData() : new ArrayList<>();
        } catch (EzvizApiException e) {
            log.error("[萤石云] 获取设备列表失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 获取设备详情
     *
     * @param deviceSerial 设备序列号
     * @return 设备信息
     */
    public EzvizDeviceInfo getDeviceInfo(String deviceSerial) {
        Map<String, String> params = new HashMap<>();
        params.put("deviceSerial", deviceSerial);
        EzvizDeviceInfoResponse resp = httpClient.postWithToken(API_DEVICE_INFO, params, EzvizDeviceInfoResponse.class);
        return resp.getData();
    }

    /**
     * 获取指定设备的摄像头通道列表
     *
     * @param deviceSerial 设备序列号
     * @return 摄像头列表
     */
    public List<EzvizCameraInfo> listCameras(String deviceSerial) {
        Map<String, String> params = new HashMap<>();
        params.put("deviceSerial", deviceSerial);
        EzvizCameraListResponse resp = httpClient.postWithToken(API_CAMERA_LIST, params, EzvizCameraListResponse.class);
        return resp.getData() != null ? resp.getData() : new ArrayList<>();
    }

    /**
     * 修改设备名称
     *
     * @param deviceSerial 设备序列号
     * @param deviceName   新设备名称
     */
    public void renameDevice(String deviceSerial, String deviceName) {
        Map<String, String> params = new HashMap<>();
        params.put("deviceSerial", deviceSerial);
        params.put("deviceName", deviceName);
        httpClient.postWithToken(API_DEVICE_RENAME, params, EzvizBaseResponse.class);
    }

    // ---------- 内部响应类型 ----------

    /** 设备列表响应 */
    static class EzvizDeviceListResponse extends EzvizBaseResponse {
        private List<EzvizDeviceInfo> data;
        public List<EzvizDeviceInfo> getData() { return data; }
        public void setData(List<EzvizDeviceInfo> data) { this.data = data; }
    }

    /** 设备详情响应 */
    static class EzvizDeviceInfoResponse extends EzvizBaseResponse {
        private EzvizDeviceInfo data;
        public EzvizDeviceInfo getData() { return data; }
        public void setData(EzvizDeviceInfo data) { this.data = data; }
    }

    /** 摄像头列表响应 */
    static class EzvizCameraListResponse extends EzvizBaseResponse {
        private List<EzvizCameraInfo> data;
        public List<EzvizCameraInfo> getData() { return data; }
        public void setData(List<EzvizCameraInfo> data) { this.data = data; }
    }

}
