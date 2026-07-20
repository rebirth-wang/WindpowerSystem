package com.fastbee.ezviz.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fastbee.ezviz.client.EzvizHttpClient;
import com.fastbee.ezviz.model.EzvizBaseResponse;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.model.*;
import com.fastbee.media.service.IPTZService;

/**
 * 萤石云 IPTZService 实现
 * <p>对接萤石云云台控制接口</p>
 *
 * <p>萤石云云台控制接口：
 * <ul>
 *   <li>开始云台控制: POST /api/lapp/device/ptz/start</li>
 *   <li>停止云台控制: POST /api/lapp/device/ptz/stop</li>
 *   <li>预置点设置: POST /api/lapp/preset/set</li>
 *   <li>调用预置点: POST /api/lapp/preset/move</li>
 *   <li>清除预置点: POST /api/lapp/preset/clear</li>
 *   <li>查询预置点列表: POST /api/lapp/preset/list</li>
 * </ul>
 * </p>
 *
 * <p>云台方向（direction）：
 * 0-上, 1-下, 2-左, 3-右, 4-左上, 5-左下, 6-右上, 7-右下,
 * 8-放大, 9-缩小, 10-近焦距, 11-远焦距</p>
 *
 * @author fastbee
 */
@Slf4j
@Service(ChannelStreamType.PTZ_SERVICE + ChannelStreamType.EZVIZ)
public class EzvizPtzServiceImpl implements IPTZService {

    public static final String BEAN_NAME = ChannelStreamType.PTZ_SERVICE + ChannelStreamType.EZVIZ;

    /** 开始云台控制 */
    private static final String API_PTZ_START   = "/api/lapp/device/ptz/start";
    /** 停止云台控制 */
    private static final String API_PTZ_STOP    = "/api/lapp/device/ptz/stop";
    /** 设置预置点 */
    private static final String API_PRESET_SET  = "/api/lapp/preset/set";
    /** 调用预置点 */
    private static final String API_PRESET_MOVE = "/api/lapp/preset/move";
    /** 清除预置点 */
    private static final String API_PRESET_CLEAR= "/api/lapp/preset/clear";
    /** 查询预置点列表 */
    private static final String API_PRESET_LIST = "/api/lapp/preset/list";

    private final EzvizHttpClient httpClient;

    public EzvizPtzServiceImpl(EzvizHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Boolean ptz(CommonChannel channel, PtzInput input) {
        // 转换为 FrontEndControlCodeForPTZ 后调用
        FrontEndControlCodeForPTZ code = new FrontEndControlCodeForPTZ();
        return ptz(channel, code);
    }

    @Override
    public Boolean frontEndCommand(CommonChannel channel, Integer cmdCode, Integer parameter1, Integer parameter2, Integer combindCode) {
        log.warn("[萤石云] frontEndCommand 不支持低级前端控制命令");
        return false;
    }

    @Override
    public Boolean ptz(CommonChannel channel, FrontEndControlCodeForPTZ frontEndControlCode) {
        String deviceSerial = channel.getDeviceId();
        int channelNo = parseChannelNo(channel.getChannelId(), deviceSerial);

        // 根据 pan/tilt/zoom 确定方向
        Integer direction = resolveDirection(frontEndControlCode);
        if (direction == null) {
            // 停止控制
            return ptzStop(deviceSerial, channelNo);
        }

        int speed = resolveSpeed(frontEndControlCode);
        log.info("[萤石云] 云台控制开始, deviceSerial={}, channelNo={}, direction={}, speed={}", deviceSerial, channelNo, direction, speed);

        Map<String, String> params = new HashMap<>();
        params.put("deviceSerial", deviceSerial);
        params.put("channelNo", String.valueOf(channelNo));
        params.put("direction", String.valueOf(direction));
        params.put("speed", String.valueOf(speed));
        httpClient.postWithToken(API_PTZ_START, params, EzvizBaseResponse.class);
        return true;
    }

    @Override
    public Boolean preset(CommonChannel channel, FrontEndControlCodeForPreset frontEndControlCode) {
        String deviceSerial = channel.getDeviceId();
        int channelNo = parseChannelNo(channel.getChannelId(), deviceSerial);
        Integer code = frontEndControlCode.getCode();
        Integer presetId = frontEndControlCode.getPresetId();

        Map<String, String> params = new HashMap<>();
        params.put("deviceSerial", deviceSerial);
        params.put("channelNo", String.valueOf(channelNo));
        params.put("index", String.valueOf(presetId));

        if (code == 1) {
            // 设置预置点
            if (frontEndControlCode.getPresetName() != null) {
                params.put("name", frontEndControlCode.getPresetName());
            }
            log.info("[萤石云] 设置预置点, deviceSerial={}, channelNo={}, presetId={}", deviceSerial, channelNo, presetId);
            httpClient.postWithToken(API_PRESET_SET, params, EzvizBaseResponse.class);
        } else if (code == 2) {
            // 调用预置点
            log.info("[萤石云] 调用预置点, deviceSerial={}, channelNo={}, presetId={}", deviceSerial, channelNo, presetId);
            httpClient.postWithToken(API_PRESET_MOVE, params, EzvizBaseResponse.class);
        } else if (code == 3) {
            // 删除预置点
            log.info("[萤石云] 删除预置点, deviceSerial={}, channelNo={}, presetId={}", deviceSerial, channelNo, presetId);
            httpClient.postWithToken(API_PRESET_CLEAR, params, EzvizBaseResponse.class);
        }
        return true;
    }

    @Override
    public Boolean fi(CommonChannel channel, FrontEndControlCodeForFI frontEndControlCode) {
        log.warn("[萤石云] FI 聚焦控制暂不支持独立接口，请使用 PTZ 放大缩小指令");
        return false;
    }

    @Override
    public Boolean tour(CommonChannel channel, FrontEndControlCodeForTour frontEndControlCode) {
        log.warn("[萤石云] 巡航控制暂不支持");
        return false;
    }

    @Override
    public Boolean scan(CommonChannel channel, FrontEndControlCodeForScan frontEndControlCode) {
        log.warn("[萤石云] 扫描控制暂不支持");
        return false;
    }

    @Override
    public Boolean auxiliary(CommonChannel channel, FrontEndControlCodeForAuxiliary frontEndControlCode) {
        log.warn("[萤石云] 辅助开关控制暂不支持");
        return false;
    }

    @Override
    public Boolean wiper(CommonChannel channel, FrontEndControlCodeForWiper frontEndControlCode) {
        log.warn("[萤石云] 雨刷控制暂不支持");
        return false;
    }

    @Override
    public List<Preset> queryPresetList(CommonChannel channel) {
        String deviceSerial = channel.getDeviceId();
        int channelNo = parseChannelNo(channel.getChannelId(), deviceSerial);
        log.info("[萤石云] 查询预置点列表, deviceSerial={}, channelNo={}", deviceSerial, channelNo);

        Map<String, String> params = new HashMap<>();
        params.put("deviceSerial", deviceSerial);
        params.put("channelNo", String.valueOf(channelNo));

        EzvizPresetListResponse resp = httpClient.postWithToken(API_PRESET_LIST, params, EzvizPresetListResponse.class);
        return convertPresets(resp);
    }

    /**
     * 停止云台控制
     */
    private Boolean ptzStop(String deviceSerial, int channelNo) {
        log.info("[萤石云] 停止云台控制, deviceSerial={}, channelNo={}", deviceSerial, channelNo);
        Map<String, String> params = new HashMap<>();
        params.put("deviceSerial", deviceSerial);
        params.put("channelNo", String.valueOf(channelNo));
        httpClient.postWithToken(API_PTZ_STOP, params, EzvizBaseResponse.class);
        return true;
    }

    /**
     * 根据 FrontEndControlCodeForPTZ 解析萤石云方向码
     * 萤石云: 0-上,1-下,2-左,3-右,4-左上,5-左下,6-右上,7-右下,8-放大,9-缩小,10-近焦距,11-远焦距
     * 返回 null 代表停止
     */
    private Integer resolveDirection(FrontEndControlCodeForPTZ code) {
        Integer pan = code.getPan();
        Integer tilt = code.getTilt();
        Integer zoom = code.getZoom();

        if (zoom != null) {
            return zoom == 1 ? 8 : 9; // 1-放大 → 8, 0-缩小 → 9
        }
        if (pan == null && tilt == null) {
            return null; // 停止
        }
        if (tilt != null && pan == null) {
            return tilt == 0 ? 0 : 1; // 0-上, 1-下
        }
        if (pan != null && tilt == null) {
            return pan == 0 ? 2 : 3; // 0-左, 1-右
        }
        // 斜方向
        if (tilt == 0 && pan == 0) return 4; // 左上
        if (tilt == 1 && pan == 0) return 5; // 左下
        if (tilt == 0 && pan == 1) return 6; // 右上
        if (tilt == 1 && pan == 1) return 7; // 右下
        return null;
    }

    /**
     * 取速度值（1-7），默认3
     */
    private int resolveSpeed(FrontEndControlCodeForPTZ code) {
        Integer speed = code.getPanSpeed();
        if (speed == null) speed = code.getTiltSpeed();
        if (speed == null) speed = code.getZoomSpeed();
        if (speed == null || speed <= 0) return 3;
        return Math.min(speed, 7);
    }

    private int parseChannelNo(String channelId, String deviceSerial) {
        if (channelId == null || channelId.isEmpty()) return 1;
        String noStr = channelId.startsWith(deviceSerial + "_")
                ? channelId.substring(deviceSerial.length() + 1)
                : channelId;
        try {
            return Integer.parseInt(noStr);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private List<Preset> convertPresets(EzvizPresetListResponse resp) {
        List<Preset> list = new ArrayList<>();
        if (resp.getData() == null) return list;
        com.alibaba.fastjson2.JSONArray array =
                com.alibaba.fastjson2.JSON.parseArray(com.alibaba.fastjson2.JSON.toJSONString(resp.getData()));
        if (array == null) return list;
        for (int i = 0; i < array.size(); i++) {
            com.alibaba.fastjson2.JSONObject item = array.getJSONObject(i);
            Preset p = new Preset();
            Integer idx = item.getInteger("index");
            p.setPresetId(idx != null ? String.valueOf(idx) : null);
            p.setPresetName(item.getString("name"));
            list.add(p);
        }
        return list;
    }

    // ---------- 内部响应类型 ----------

    static class EzvizPresetListResponse extends EzvizBaseResponse {
        private Object data;
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }

}
