package com.fastbee.onvif.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.enums.ErrorCode;
import com.fastbee.media.model.*;
import com.fastbee.media.service.IPTZService;
import com.fastbee.onvif.domain.OnvifDeviceChannel;
import com.fastbee.onvif.service.IOnvifDeviceChannelService;
import com.fastbee.onvif.util.OnvifClient;

/**
 * ONVIF PTZ 云台控制服务实现
 * 通过 OnvifClient 直接调用 ONVIF PTZ Service SOAP 接口
 *
 * <p>ONVIF PTZ Service 规范对应（ONVIF PTZ Service Specification 22.06）：
 * <ul>
 *   <li>{@code ptz(PtzInput)} → {@code ContinuousMove(profileToken, velocity{PanTilt, Zoom})} / {@code Stop}</li>
 *   <li>{@code ptz(FrontEndControlCodeForPTZ)} → 方向速度转换后调用 {@code ContinuousMove} / {@code Stop}</li>
 *   <li>{@code preset} → {@code SetPreset} / {@code GotoPreset} / {@code RemovePreset}</li>
 *   <li>{@code fi(FrontEndControlCodeForFI)} → PTZ {@code RelativeMove}（Zoom 轴）</li>
 *   <li>{@code tour} → {@code GotoPreset} 实现巡航</li>
 *   <li>{@code scan} → {@code ContinuousMove} 持续移动实现扫描</li>
 *   <li>{@code auxiliary} → {@code SendAuxiliaryCommand}</li>
 *   <li>{@code wiper} → {@code SendAuxiliaryCommand("tt:Wiper|On/Off")}</li>
 *   <li>{@code queryPresetList} → {@code GetPresets(profileToken)}</li>
 * </ul>
 *
 * @author fastbee
 */
@Slf4j
@Service(ChannelStreamType.PTZ_SERVICE + ChannelStreamType.ONVIF)
public class PtzServiceForOnvifImpl implements IPTZService {

    /** FastBee 速度值到 ONVIF 浮点值的归一化比例（FastBee: 0~100, ONVIF: 0.0~1.0） */
    private static final double SPEED_SCALE = 0.01;

    /** 默认 PTZ 移动速度（对应 ONVIF 速度值 0.5） */
    private static final double DEFAULT_SPEED = 0.5;

    @Autowired
    private IOnvifDeviceChannelService onvifDeviceChannelService;

    @Autowired
    private OnvifClient onvifClient;

    // -------------------------------------------------------------------------
    // IPTZService 接口实现
    // -------------------------------------------------------------------------

    /**
     * PTZ 方向控制（基于 PtzInput 速度值）
     * 对应 ONVIF ContinuousMove / Stop
     * 速度值 leftRight/upDown/inOut 取值：
     *   &gt; 0 表示右/上/放大，&lt; 0 表示左/下/缩小，= 0 表示停止
     *
     * @param channel 通用通道
     * @param input   PTZ 输入（leftRight/upDown/inOut/moveSpeed）
     * @return 操作结果
     */
    @Override
    public Boolean ptz(CommonChannel channel, PtzInput input) {
        log.info("[ONVIF PTZ] 方向控制 channelId: {}, lr: {}, ud: {}, io: {}",
                channel.getId(), input.getLeftRight(), input.getUpDown(), input.getInOut());
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);
        ensureProfileToken(onvifChannel);

        // 判断是否为停止指令
        if (isZeroInput(input.getLeftRight(), input.getUpDown(), input.getInOut())) {
            return ptzStopInternal(onvifChannel);
        }

        // 将速度值归一化到 -1.0~1.0
        double speedScale = input.getMoveSpeed() != null
                ? normalizeSpeed(input.getMoveSpeed()) : DEFAULT_SPEED;
        double panVelocity  = toVelocity(input.getLeftRight(), speedScale);
        double tiltVelocity = toVelocity(input.getUpDown(), speedScale);
        double zoomVelocity = toVelocity(input.getInOut(), speedScale);

        try {
            onvifClient.continuousMove(onvifChannel, panVelocity, tiltVelocity, zoomVelocity);
            log.info("[ONVIF PTZ] ContinuousMove 成功 channelId: {}", channel.getId());
            return true;
        } catch (Exception e) {
            log.error("[ONVIF PTZ] ContinuousMove 失败 channelId: {}, 错误: {}", channel.getId(), e.getMessage());
            throw new ServiceException(ErrorCode.ERROR100.getCode(), "PTZ 控制失败: " + e.getMessage());
        }
    }

    /**
     * 前端控制码路由（通用入口）
     *
     * @param channel       通用通道
     * @param cmdCode       控制命令码
     * @param parameter1    参数1
     * @param parameter2    参数2
     * @param combindCode   组合控制码
     * @return 操作结果
     */
    @Override
    public Boolean frontEndCommand(CommonChannel channel, Integer cmdCode, Integer parameter1,
                                   Integer parameter2, Integer combindCode) {
        log.info("[ONVIF PTZ] 前端控制 channelId: {}, cmd: {}", channel.getId(), cmdCode);
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);
        ensureProfileToken(onvifChannel);

        if (cmdCode == null) return false;

        try {
            switch (cmdCode) {
                case 0: // 停止
                    return ptzStopInternal(onvifChannel);
                case 21: // 向左
                    onvifClient.continuousMove(onvifChannel, -DEFAULT_SPEED, 0, 0);
                    return true;
                case 22: // 向右
                    onvifClient.continuousMove(onvifChannel, DEFAULT_SPEED, 0, 0);
                    return true;
                case 23: // 向上
                    onvifClient.continuousMove(onvifChannel, 0, DEFAULT_SPEED, 0);
                    return true;
                case 24: // 向下
                    onvifClient.continuousMove(onvifChannel, 0, -DEFAULT_SPEED, 0);
                    return true;
                case 11: // 放大（Zoom In）
                    onvifClient.continuousMove(onvifChannel, 0, 0, DEFAULT_SPEED);
                    return true;
                case 12: // 缩小（Zoom Out）
                    onvifClient.continuousMove(onvifChannel, 0, 0, -DEFAULT_SPEED);
                    return true;
                default:
                    log.warn("[ONVIF PTZ] 未识别的控制码: {}", cmdCode);
                    return false;
            }
        } catch (Exception e) {
            log.error("[ONVIF PTZ] 前端控制失败 channelId: {}, 错误: {}", channel.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * PTZ 方向控制（基于 FrontEndControlCodeForPTZ）
     * 对应 ONVIF ContinuousMove / Stop
     *
     * @param channel            通用通道
     * @param frontEndControlCode 方向控制码
     * @return 操作结果
     */
    @Override
    public Boolean ptz(CommonChannel channel, FrontEndControlCodeForPTZ frontEndControlCode) {
        log.info("[ONVIF PTZ] FEC 方向控制 channelId: {}", channel.getId());
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);
        ensureProfileToken(onvifChannel);

        Integer pan  = frontEndControlCode.getPan();
        Integer tilt = frontEndControlCode.getTilt();
        Integer zoom = frontEndControlCode.getZoom();

        // 三个方向均为 null 则视为停止
        if (pan == null && tilt == null && zoom == null) {
            return ptzStopInternal(onvifChannel);
        }

        double panSpeedNorm  = normalizeSpeed(frontEndControlCode.getPanSpeed());
        double tiltSpeedNorm = normalizeSpeed(frontEndControlCode.getTiltSpeed());
        double zoomSpeedNorm = normalizeSpeed(frontEndControlCode.getZoomSpeed());

        // pan: 0=左, 1=右; tilt: 0=上, 1=下; zoom: 0=缩小, 1=放大
        double panVelocity  = pan  != null ? (pan  == 1 ? panSpeedNorm  : -panSpeedNorm)  : 0.0;
        double tiltVelocity = tilt != null ? (tilt == 0 ? tiltSpeedNorm : -tiltSpeedNorm) : 0.0;
        double zoomVelocity = zoom != null ? (zoom == 1 ? zoomSpeedNorm : -zoomSpeedNorm) : 0.0;

        try {
            onvifClient.continuousMove(onvifChannel, panVelocity, tiltVelocity, zoomVelocity);
            log.info("[ONVIF PTZ] FEC ContinuousMove 成功 channelId: {}", channel.getId());
            return true;
        } catch (Exception e) {
            log.error("[ONVIF PTZ] FEC ContinuousMove 失败 channelId: {}, 错误: {}", channel.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * 预置位控制
     * 对应 ONVIF SetPreset / GotoPreset / RemovePreset
     * code: 1=设置, 2=调用, 3=删除
     *
     * @param channel             通用通道
     * @param frontEndControlCode 预置位控制码
     * @return 操作结果
     */
    @Override
    public Boolean preset(CommonChannel channel, FrontEndControlCodeForPreset frontEndControlCode) {
        log.info("[ONVIF PTZ] 预置位 channelId: {}, code: {}, presetId: {}",
                channel.getId(), frontEndControlCode.getCode(), frontEndControlCode.getPresetId());
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);
        ensureProfileToken(onvifChannel);

        Integer code     = frontEndControlCode.getCode();
        String presetId  = frontEndControlCode.getPresetId() != null
                ? String.valueOf(frontEndControlCode.getPresetId()) : null;
        String presetName = frontEndControlCode.getPresetName();

        if (code == null) return false;

        try {
            switch (code) {
                case 1: // 设置预置位（SetPreset）
                    String newPresetToken = onvifClient.setPreset(onvifChannel, presetName);
                    log.info("[ONVIF PTZ] SetPreset 成功, presetToken: {}", newPresetToken);
                    return true;
                case 2: // 调用预置位（GotoPreset）
                    if (presetId == null) {
                        throw new ServiceException(ErrorCode.ERROR400.getCode(), "调用预置位时 presetId 不能为空");
                    }
                    onvifClient.gotoPreset(onvifChannel, presetId);
                    log.info("[ONVIF PTZ] GotoPreset 成功, presetId: {}", presetId);
                    return true;
                case 3: // 删除预置位（RemovePreset）
                    if (presetId == null) {
                        throw new ServiceException(ErrorCode.ERROR400.getCode(), "删除预置位时 presetId 不能为空");
                    }
                    onvifClient.removePreset(onvifChannel, presetId);
                    log.info("[ONVIF PTZ] RemovePreset 成功, presetId: {}", presetId);
                    return true;
                default:
                    log.warn("[ONVIF PTZ] 未识别的预置位操作码: {}", code);
                    return false;
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("[ONVIF PTZ] 预置位操作失败 channelId: {}, 错误: {}", channel.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * 焦距/光圈控制（F/I）
     * 使用 PTZ RelativeMove 控制 Zoom 轴实现变焦；
     * Focus 调节通过 Imaging Service（此处通过 PTZ Zoom 近似实现）
     * 对应 ONVIF PTZ RelativeMove（Zoom）
     *
     * @param channel             通用通道
     * @param frontEndControlCode FI 控制码
     * @return 操作结果
     */
    @Override
    public Boolean fi(CommonChannel channel, FrontEndControlCodeForFI frontEndControlCode) {
        log.info("[ONVIF PTZ] FI 控制 channelId: {}, iris: {}, focus: {}",
                channel.getId(), frontEndControlCode.getIris(), frontEndControlCode.getFocus());
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);
        ensureProfileToken(onvifChannel);

        Integer iris  = frontEndControlCode.getIris();
        Integer focus = frontEndControlCode.getFocus();
        double irisSpeed  = normalizeSpeed(frontEndControlCode.getIrisSpeed());
        double focusSpeed = normalizeSpeed(frontEndControlCode.getFocusSpeed());

        try {
            if (iris != null) {
                // iris: 0=缩小光圈, 1=放大光圈 → 通过 PTZ Zoom 近似
                double zoomVelocity = iris == 1 ? irisSpeed : -irisSpeed;
                onvifClient.continuousMove(onvifChannel, 0, 0, zoomVelocity);
                log.info("[ONVIF PTZ] 光圈控制（ContinuousMove Zoom）成功 channelId: {}", channel.getId());
            }
            if (focus != null) {
                // focus: 0=近焦, 1=远焦 → 通过 PTZ Zoom 近似（Imaging Service 更精确但需单独实现）
                double zoomVelocity = focus == 1 ? focusSpeed : -focusSpeed;
                onvifClient.continuousMove(onvifChannel, 0, 0, zoomVelocity);
                log.info("[ONVIF PTZ] 焦距控制（ContinuousMove Zoom）成功 channelId: {}", channel.getId());
            }
            return true;
        } catch (Exception e) {
            log.error("[ONVIF PTZ] FI 控制失败 channelId: {}, 错误: {}", channel.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * 巡航控制
     * ONVIF 通过 GotoPreset 序列实现巡航；code=5 开始，code=6 停止
     * 对应 ONVIF GotoPreset（循环调用预置位序列）
     *
     * @param channel             通用通道
     * @param frontEndControlCode 巡航控制码
     * @return 操作结果
     */
    @Override
    public Boolean tour(CommonChannel channel, FrontEndControlCodeForTour frontEndControlCode) {
        log.info("[ONVIF PTZ] 巡航 channelId: {}, code: {}", channel.getId(), frontEndControlCode.getCode());
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);
        ensureProfileToken(onvifChannel);

        Integer code = frontEndControlCode.getCode();
        if (code == null) return false;

        try {
            switch (code) {
                case 5: // 开始巡航：先 GotoHomePosition（归位）
                    onvifClient.gotoHomePosition(onvifChannel);
                    log.info("[ONVIF PTZ] 巡航开始（GotoHomePosition） channelId: {}", channel.getId());
                    return true;
                case 6: // 停止巡航
                    return ptzStopInternal(onvifChannel);
                case 2: // 调用指定巡航点（GotoPreset）
                    if (frontEndControlCode.getPresetId() != null) {
                        onvifClient.gotoPreset(onvifChannel, String.valueOf(frontEndControlCode.getPresetId()));
                        return true;
                    }
                    return false;
                default:
                    log.warn("[ONVIF PTZ] 未识别的巡航控制码: {}", code);
                    return false;
            }
        } catch (Exception e) {
            log.error("[ONVIF PTZ] 巡航控制失败 channelId: {}, 错误: {}", channel.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * 自动扫描
     * 通过 ONVIF ContinuousMove 持续移动实现扫描；code=1 开始，code=5 停止
     * 对应 ONVIF ContinuousMove（Pan 方向持续移动）
     *
     * @param channel             通用通道
     * @param frontEndControlCode 扫描控制码
     * @return 操作结果
     */
    @Override
    public Boolean scan(CommonChannel channel, FrontEndControlCodeForScan frontEndControlCode) {
        log.info("[ONVIF PTZ] 扫描 channelId: {}, code: {}", channel.getId(), frontEndControlCode.getCode());
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);
        ensureProfileToken(onvifChannel);

        Integer code      = frontEndControlCode.getCode();
        double  scanSpeed = frontEndControlCode.getScanSpeed() != null
                ? normalizeSpeed(frontEndControlCode.getScanSpeed()) : DEFAULT_SPEED;

        if (code == null) return false;

        try {
            switch (code) {
                case 1: // 开始自动扫描（ContinuousMove - Pan 右移）
                    onvifClient.continuousMove(onvifChannel, scanSpeed, 0, 0);
                    log.info("[ONVIF PTZ] 扫描开始 channelId: {}", channel.getId());
                    return true;
                case 5: // 停止自动扫描
                    return ptzStopInternal(onvifChannel);
                default:
                    log.warn("[ONVIF PTZ] 未识别的扫描控制码: {}", code);
                    return false;
            }
        } catch (Exception e) {
            log.error("[ONVIF PTZ] 扫描控制失败 channelId: {}, 错误: {}", channel.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * 辅助功能控制
     * 对应 ONVIF PTZ SendAuxiliaryCommand
     * code: 1=开, 2=关；auxiliaryId 为辅助编号
     *
     * @param channel             通用通道
     * @param frontEndControlCode 辅助控制码
     * @return 操作结果
     */
    @Override
    public Boolean auxiliary(CommonChannel channel, FrontEndControlCodeForAuxiliary frontEndControlCode) {
        log.info("[ONVIF PTZ] 辅助功能 channelId: {}, code: {}, auxId: {}",
                channel.getId(), frontEndControlCode.getCode(), frontEndControlCode.getAuxiliaryId());
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);
        ensureProfileToken(onvifChannel);

        Integer code = frontEndControlCode.getCode();
        Integer auxId = frontEndControlCode.getAuxiliaryId();
        String action = (code != null && code == 1) ? "On" : "Off";
        // ONVIF AuxiliaryData 格式：tt:Aux|{auxId}|{action} 或自定义格式
        String auxData = String.format("tt:Aux|%d|%s", auxId != null ? auxId : 1, action);

        try {
            onvifClient.sendAuxiliaryCommand(onvifChannel, auxData);
            log.info("[ONVIF PTZ] SendAuxiliaryCommand 成功, data: {}", auxData);
            return true;
        } catch (Exception e) {
            log.error("[ONVIF PTZ] 辅助功能控制失败 channelId: {}, 错误: {}", channel.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * 雨刷控制
     * 对应 ONVIF PTZ SendAuxiliaryCommand("tt:Wiper|On") / ("tt:Wiper|Off")
     * code: 1=开启, 2=关闭
     *
     * @param channel             通用通道
     * @param frontEndControlCode 雨刷控制码
     * @return 操作结果
     */
    @Override
    public Boolean wiper(CommonChannel channel, FrontEndControlCodeForWiper frontEndControlCode) {
        log.info("[ONVIF PTZ] 雨刷控制 channelId: {}, code: {}", channel.getId(), frontEndControlCode.getCode());
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);
        ensureProfileToken(onvifChannel);

        Integer code = frontEndControlCode.getCode();
        // ONVIF Wiper 命令格式：tt:Wiper|On 或 tt:Wiper|Off
        String wiperData = (code != null && code == 1) ? "tt:Wiper|On" : "tt:Wiper|Off";

        try {
            onvifClient.sendAuxiliaryCommand(onvifChannel, wiperData);
            log.info("[ONVIF PTZ] 雨刷 {} 成功 channelId: {}", wiperData, channel.getId());
            return true;
        } catch (Exception e) {
            log.error("[ONVIF PTZ] 雨刷控制失败 channelId: {}, 错误: {}", channel.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * 查询预置位列表
     * 对应 ONVIF PTZ GetPresets(profileToken)
     *
     * @param channel 通用通道
     * @return 预置位列表
     */
    @Override
    public List<Preset> queryPresetList(CommonChannel channel) {
        log.info("[ONVIF PTZ] 查询预置位列表 channelId: {}", channel.getId());
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);
        ensureProfileToken(onvifChannel);

        List<Preset> presetList = new ArrayList<>();
        try {
            List<Map<String, String>> onvifPresets = onvifClient.getPresets(onvifChannel);
            for (Map<String, String> presetMap : onvifPresets) {
                Preset preset = new Preset();
                preset.setPresetId(presetMap.get("token"));
                preset.setPresetName(presetMap.get("name"));
                presetList.add(preset);
            }
            log.info("[ONVIF PTZ] GetPresets 成功，共 {} 个预置位 channelId: {}", presetList.size(), channel.getId());
        } catch (Exception e) {
            log.error("[ONVIF PTZ] 查询预置位失败 channelId: {}, 错误: {}", channel.getId(), e.getMessage());
        }
        return presetList;
    }

    // -------------------------------------------------------------------------
    // 内部工具方法
    // -------------------------------------------------------------------------

    private OnvifDeviceChannel resolveChannel(CommonChannel channel) {
        Integer id = extractChannelId(channel);
        OnvifDeviceChannel onvifChannel = onvifDeviceChannelService.selectOnvifDeviceChannelById(id);
        if (onvifChannel == null) {
            throw new ServiceException(ErrorCode.ERROR400.getCode(), "ONVIF 通道不存在，id: " + id);
        }
        return onvifChannel;
    }

    private Integer extractChannelId(CommonChannel channel) {
        try {
            return Integer.parseInt(channel.getChannelId());
        } catch (NumberFormatException e) {
            return Math.toIntExact(channel.getId());
        }
    }

    private void ensureProfileToken(OnvifDeviceChannel channel) {
        if (channel.getProfileToken() == null || channel.getProfileToken().isEmpty()) {
            try {
                List<String> profiles = onvifClient.getProfiles(channel);
                if (!profiles.isEmpty()) {
                    channel.setProfileToken(profiles.get(0));
                    onvifDeviceChannelService.updateWithCache(channel);
                }
            } catch (Exception e) {
                throw new ServiceException(ErrorCode.ERROR100.getCode(),
                        "无法获取 Profile Token，请先查询设备信息: " + e.getMessage());
            }
        }
    }

    /**
     * 内部停止 PTZ
     * 对应 ONVIF PTZ Stop(profileToken, panTilt=true, zoom=true)
     */
    private Boolean ptzStopInternal(OnvifDeviceChannel channel) {
        try {
            onvifClient.ptzStop(channel, true, true);
            log.info("[ONVIF PTZ] Stop 成功 channelId: {}", channel.getId());
            return true;
        } catch (Exception e) {
            log.error("[ONVIF PTZ] Stop 失败 channelId: {}, 错误: {}", channel.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * 检查速度输入是否全为 0（停止条件）
     */
    private boolean isZeroInput(Integer... values) {
        for (Integer v : values) {
            if (v != null && v != 0) return false;
        }
        return true;
    }

    /**
     * 将方向值（正/负/零）与速度组合为 ONVIF 速度（-1.0 ~ 1.0）
     *
     * @param direction 方向值（&gt;0 正方向，&lt;0 负方向，0 停止）
     * @param speed     归一化速度（0.0~1.0）
     */
    private double toVelocity(Integer direction, double speed) {
        if (direction == null || direction == 0) return 0.0;
        return direction > 0 ? speed : -speed;
    }

    /**
     * 将 FastBee 速度值（0~100）归一化为 ONVIF 浮点速度（0.0~1.0）
     * 若输入为 null 或 0，返回默认速度
     */
    private double normalizeSpeed(Integer speed) {
        if (speed == null || speed == 0) return DEFAULT_SPEED;
        return Math.min(1.0, Math.abs(speed) * SPEED_SCALE);
    }
}
