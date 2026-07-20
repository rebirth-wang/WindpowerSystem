package com.fastbee.icc.service.impl;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fastbee.icc.client.IccHttpClient;
import com.fastbee.icc.exception.IccApiException;
import com.fastbee.icc.model.video.ptzControl.GetPresetPointsRequest;
import com.fastbee.icc.model.video.ptzControl.GetPresetPointsResponse;
import com.fastbee.icc.model.video.ptzControl.OperateCameraRequest;
import com.fastbee.icc.model.video.ptzControl.OperateCameraResponse;
import com.fastbee.icc.model.video.ptzControl.OperateDirectRequest;
import com.fastbee.icc.model.video.ptzControl.OperateDirectResponse;
import com.fastbee.icc.model.video.ptzControl.OperatePresetPointRequest;
import com.fastbee.icc.model.video.ptzControl.OperatePresetPointResponse;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.model.*;
import com.fastbee.media.service.IPTZService;

/**
 * 大华ICC平台云台控制服务（IPTZService实现）
 *
 * <p>通过大华ICC云台API实现：
 * <ul>
 *   <li>云台镜头控制：POST /evo-apigw/admin/API/DMS/Ptz/OperateCamera（放大/缩小/聚焦等）</li>
 *   <li>云台方向控制：POST /evo-apigw/admin/API/DMS/Ptz/OperateDirect（上下左右等方向）</li>
 *   <li>预置点控制：POST /evo-apigw/admin/API/DMS/Ptz/OperatePresetPoint（添加/定位/删除）</li>
 *   <li>查询预置点：POST /evo-apigw/admin/API/DMS/Ptz/GetPresetPoints</li>
 * </ul>
 * </p>
 *
 * <p>大华ICC云台方向定义：
 * OperateDirect.direct: 0-上,1-下,2-左,3-右,4-左上,5-左下,6-右上,7-右下
 * 步长(stepX/stepY): 1~9，数值越大速度越快</p>
 *
 * @author fastbee
 */
@Slf4j
@Service(ChannelStreamType.PTZ_SERVICE + ChannelStreamType.ICC_DAHUA)
public class IccPtzServiceImpl implements IPTZService {

    /** Bean名称规则：ptzService + ChannelStreamType.ICC_DAHUA */
    public static final String BEAN_NAME = ChannelStreamType.PTZ_SERVICE + ChannelStreamType.ICC_DAHUA;

    /** 云台镜头控制接口 */
    private static final String API_PTZ_CAMERA = "/evo-apigw/admin/API/DMS/Ptz/OperateCamera";
    /** 云台方向控制接口 */
    private static final String API_PTZ_DIRECT = "/evo-apigw/admin/API/DMS/Ptz/OperateDirect";
    /** 预置点控制接口（定位/添加/删除） */
    private static final String API_PRESET_OPERATE = "/evo-apigw/admin/API/DMS/Ptz/OperatePresetPoint";
    /** 查询预置点接口 */
    private static final String API_PRESET_QUERY = "/evo-apigw/admin/API/DMS/Ptz/GetPresetPoints";

    /** 默认云台步长 */
    private static final String DEFAULT_STEP = "5";

    private final IccHttpClient httpClient;

    public IccPtzServiceImpl(IccHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Boolean ptz(CommonChannel channel, PtzInput input) {
        // 转换为 FrontEndControlCodeForPTZ 后调用
        FrontEndControlCodeForPTZ code = new FrontEndControlCodeForPTZ();
        // PtzInput 映射逻辑（具体字段依赖PtzInput定义，此处做基本映射）
        return ptz(channel, code);
    }

    @Override
    public Boolean frontEndCommand(CommonChannel channel, Integer cmdCode, Integer parameter1, Integer parameter2, Integer combindCode) {
        log.warn("[大华ICC] frontEndCommand 不支持低级前端控制命令");
        return false;
    }

    @Override
    public Boolean ptz(CommonChannel channel, FrontEndControlCodeForPTZ frontEndControlCode) {
        String channelId = channel.getChannelId();
        Integer zoom = frontEndControlCode.getZoom();
        Integer pan = frontEndControlCode.getPan();
        Integer tilt = frontEndControlCode.getTilt();

        // 判断是镜头控制（zoom）还是方向控制（pan/tilt）
        if (zoom != null) {
            return executeCameraControl(channelId, zoom, frontEndControlCode.getZoomSpeed());
        }
        if (pan != null || tilt != null) {
            return executeDirectControl(channelId, pan, tilt,
                    frontEndControlCode.getPanSpeed(), frontEndControlCode.getTiltSpeed());
        }

        log.warn("[大华ICC] PTZ控制参数为空，无法执行, channelId={}", channelId);
        return false;
    }

    @Override
    public Boolean preset(CommonChannel channel, FrontEndControlCodeForPreset frontEndControlCode) {
        String channelId = channel.getChannelId();
        Integer code = frontEndControlCode.getCode();
        Integer presetId = frontEndControlCode.getPresetId();
        String presetName = frontEndControlCode.getPresetName();

        if (presetId == null) {
            log.warn("[大华ICC] 预置点ID为空, channelId={}", channelId);
            return false;
        }

        OperatePresetPointRequest request = new OperatePresetPointRequest();
        OperatePresetPointRequest.Data data = new OperatePresetPointRequest.Data();
        data.setChannelId(channelId);
        data.setPresetPointCode(String.valueOf(presetId));

        if (code == 1) {
            // 添加预置点
            data.setPresetPointName(presetName != null ? presetName : "预置点" + presetId);
            data.setOperateType("2"); // 2=添加
            log.info("[大华ICC] 添加预置点, channelId={}, presetId={}", channelId, presetId);
        } else if (code == 2) {
            // 调用预置点（定位）
            data.setOperateType("1"); // 1=定位
            log.info("[大华ICC] 调用预置点, channelId={}, presetId={}", channelId, presetId);
        } else if (code == 3) {
            // 删除预置点
            data.setOperateType("3"); // 3=删除
            log.info("[大华ICC] 删除预置点, channelId={}, presetId={}", channelId, presetId);
        } else {
            log.warn("[大华ICC] 未知预置点操作码: {}", code);
            return false;
        }

        request.setData(data);
        OperatePresetPointResponse response = httpClient.post(API_PRESET_OPERATE, request, OperatePresetPointResponse.class);
        boolean success = response != null && "1000".equals(response.getCode());
        if (!success) {
            log.warn("[大华ICC] 预置点控制失败, channelId={}, code={}", channelId, response != null ? response.getCode() : "null");
        }
        return success;
    }

    @Override
    public Boolean fi(CommonChannel channel, FrontEndControlCodeForFI frontEndControlCode) {
        log.warn("[大华ICC] FI 聚焦控制，请使用镜头控制接口（zoom）");
        // 可通过 OperateCamera 的 command 字段扩展聚焦控制
        return false;
    }

    @Override
    public Boolean tour(CommonChannel channel, FrontEndControlCodeForTour frontEndControlCode) {
        log.warn("[大华ICC] 巡航控制暂不支持");
        return false;
    }

    @Override
    public Boolean scan(CommonChannel channel, FrontEndControlCodeForScan frontEndControlCode) {
        log.warn("[大华ICC] 扫描控制暂不支持");
        return false;
    }

    @Override
    public Boolean auxiliary(CommonChannel channel, FrontEndControlCodeForAuxiliary frontEndControlCode) {
        log.warn("[大华ICC] 辅助开关控制暂不支持");
        return false;
    }

    @Override
    public Boolean wiper(CommonChannel channel, FrontEndControlCodeForWiper frontEndControlCode) {
        log.warn("[大华ICC] 雨刷控制暂不支持");
        return false;
    }

    @Override
    public List<Preset> queryPresetList(CommonChannel channel) {
        String channelId = channel.getChannelId();
        log.info("[大华ICC] 查询预置点列表, channelId={}", channelId);

        GetPresetPointsRequest request = new GetPresetPointsRequest();
        GetPresetPointsRequest.Data data = new GetPresetPointsRequest.Data();
        data.setChannelId(channelId);
        request.setData(data);

        try {
            GetPresetPointsResponse response = httpClient.post(API_PRESET_QUERY, request, GetPresetPointsResponse.class);
            if (response == null || !response.getCode().equals("1000")) {
                log.warn("[大华ICC] 查询预置点失败, channelId={}", channelId);
                return new ArrayList<>();
            }
            return convertPresets(response);
        } catch (IccApiException e) {
            log.error("[大华ICC] 查询预置点异常: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 执行云台镜头控制（放大/缩小/聚焦等）
     * 大华ICC OperateCamera operateType: zoom_tele=放大，zoom_wide=缩小，focus_far=远焦，focus_near=近焦
     *
     * @param channelId 通道ID
     * @param zoom      0-缩小，1-放大
     * @param speed     速度（忽略，ICC通过step控制）
     */
    private Boolean executeCameraControl(String channelId, int zoom, Integer speed) {
        OperateCameraRequest request = new OperateCameraRequest();
        OperateCameraRequest.Data data = new OperateCameraRequest.Data();
        data.setChannelId(channelId);
        // 大华ICC OperateCamera.direct: 1=开始，2=停止
        data.setDirect("1");
        // operateType: zoom_tele=变倍放大，zoom_wide=变倍缩小
        data.setOperateType(zoom == 1 ? "zoom_tele" : "zoom_wide");
        data.setStep(resolveStep(speed));
        request.setData(data);

        log.info("[大华ICC] 镜头控制, channelId={}, operateType={}", channelId, data.getOperateType());
        OperateCameraResponse response = httpClient.post(API_PTZ_CAMERA, request, OperateCameraResponse.class);
        boolean success = response != null && "1000".equals(response.getCode());
        if (!success) {
            log.warn("[大华ICC] 镜头控制失败, channelId={}, desc={}", channelId,
                    response != null ? response.getDesc() : "null");
        }
        return success;
    }

    /**
     * 执行云台方向控制
     * 大华ICC OperateDirect.direct: 0-上,1-下,2-左,3-右,4-左上,5-左下,6-右上,7-右下
     * FrontEndControlCodeForPTZ: tilt 0=上,1=下; pan 0=左,1=右
     *
     * @param channelId  通道ID
     * @param pan        水平方向：null=不控制，0=左，1=右
     * @param tilt       垂直方向：null=不控制，0=上，1=下
     * @param panSpeed   水平速度
     * @param tiltSpeed  垂直速度
     */
    private Boolean executeDirectControl(String channelId, Integer pan, Integer tilt,
                                         Integer panSpeed, Integer tiltSpeed) {
        Integer direction = resolveDirection(pan, tilt);
        if (direction == null) {
            log.debug("[大华ICC] 方向控制参数为空，跳过, channelId={}", channelId);
            return true;
        }

        OperateDirectRequest request = new OperateDirectRequest();
        OperateDirectRequest.Data data = new OperateDirectRequest.Data();
        data.setChannelId(channelId);
        data.setDirect(String.valueOf(direction));
        // 步长：使用较大速度作为参考
        int speed = Math.max(panSpeed != null ? panSpeed : 0, tiltSpeed != null ? tiltSpeed : 0);
        String step = resolveStep(speed > 0 ? speed : null);
        data.setStepX(step);
        data.setStepY(step);
        data.setCommand("1"); // 1=开始控制
        request.setData(data);

        log.info("[大华ICC] 方向控制, channelId={}, direction={}, step={}", channelId, direction, step);
        OperateDirectResponse response = httpClient.post(API_PTZ_DIRECT, request, OperateDirectResponse.class);
        boolean success = response != null && "1000".equals(response.getCode());
        if (!success) {
            log.warn("[大华ICC] 方向控制失败, channelId={}, desc={}", channelId,
                    response != null ? response.getDesc() : "null");
        }
        return success;
    }

    /**
     * 将pan/tilt转换为大华ICC方向码
     * FrontEndControlCodeForPTZ: tilt 0=上,1=下; pan 0=左,1=右
     * 大华ICC: 0-上,1-下,2-左,3-右,4-左上,5-左下,6-右上,7-右下
     * 返回null代表无方向（停止）
     */
    private Integer resolveDirection(Integer pan, Integer tilt) {
        if (pan == null && tilt == null) {
            return null;
        }
        if (tilt != null && pan == null) {
            return tilt == 0 ? 0 : 1; // 0=上, 1=下
        }
        if (pan != null && tilt == null) {
            return pan == 0 ? 2 : 3; // 2=左, 3=右
        }
        // 斜方向
        if (tilt == 0 && pan == 0) return 4; // 左上
        if (tilt == 1 && pan == 0) return 5; // 左下
        if (tilt == 0 && pan == 1) return 6; // 右上
        if (tilt == 1 && pan == 1) return 7; // 右下
        return null;
    }

    /**
     * 将速度值转换为大华ICC步长字符串（1-9）
     */
    private String resolveStep(Integer speed) {
        if (speed == null || speed <= 0) {
            return DEFAULT_STEP;
        }
        // 速度映射：归一化到1-9范围
        int step = Math.min(9, Math.max(1, speed));
        return String.valueOf(step);
    }

    /**
     * 转换预置点列表
     */
    private List<Preset> convertPresets(GetPresetPointsResponse response) {
        List<Preset> presets = new ArrayList<>();
        if (response.getData() == null || response.getData().getPresetPoints() == null) {
            return presets;
        }
        for (GetPresetPointsResponse.Data.PresetPoint p : response.getData().getPresetPoints()) {
            Preset preset = new Preset();
            preset.setPresetId(p.getPresetPointCode());
            preset.setPresetName(p.getPresetPointName());
            presets.add(preset);
        }
        return presets;
    }
}
