package com.fastbee.onvif.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.service.ICommonChannelService;
import com.fastbee.onvif.convert.OnvifDeviceChannelConvert;
import com.fastbee.onvif.domain.OnvifDeviceChannel;
import com.fastbee.onvif.domain.vo.OnvifDeviceChannelVO;
import com.fastbee.onvif.service.IOnvifDeviceChannelService;

/**
 * onvif设备通道Service业务层处理
 *
 * @author fastbee
 * @date 2026-01-06
 */
@Service
public class OnvifDeviceChannelServiceImpl implements IOnvifDeviceChannelService {

    private static final String ONVIF_CHANNEL_PREFIX = "ONVIF-";

    @Resource
    private ICommonChannelService commonChannelService;

    /** 代码生成区域 可直接覆盖**/
    /**
     * 查询onvif设备通道
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return onvif设备通道
     */
    @Override
    @Cacheable(cacheNames = "OnvifDeviceChannel", key = "#id")
    public OnvifDeviceChannel queryByIdWithCache(Integer id){
        return selectOnvifDeviceChannelById(id);
    }

    /**
     * 查询onvif设备通道
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return onvif设备通道
     */
    @Override
    @Cacheable(cacheNames = "OnvifDeviceChannel", key = "#id")
    public OnvifDeviceChannel selectOnvifDeviceChannelById(Integer id){
        if (id == null) {
            return null;
        }
        CommonChannel commonChannel = commonChannelService.getById(Long.valueOf(id));
        return toOnvifDeviceChannel(commonChannel);
    }

    /**
     * 查询onvif设备通道分页列表
     *
     * @param onvifDeviceChannel onvif设备通道
     * @return onvif设备通道
     */
    @Override
    public Page<OnvifDeviceChannelVO> pageOnvifDeviceChannelVO(OnvifDeviceChannel onvifDeviceChannel) {
        LambdaQueryWrapper<CommonChannel> lqw = buildCommonChannelQueryWrapper(onvifDeviceChannel);
        Page<CommonChannel> commonChannelPage = commonChannelService.page(new Page<>(onvifDeviceChannel.getPageNum(), onvifDeviceChannel.getPageSize()), lqw);
        Page<OnvifDeviceChannel> onvifDeviceChannelPage = new Page<>(
                commonChannelPage.getCurrent(), commonChannelPage.getSize(), commonChannelPage.getTotal());
        onvifDeviceChannelPage.setRecords(commonChannelPage.getRecords().stream()
                .map(this::toOnvifDeviceChannel)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        return OnvifDeviceChannelConvert.INSTANCE.convertOnvifDeviceChannelVOPage(onvifDeviceChannelPage);
    }

    /**
     * 查询onvif设备通道列表
     *
     * @param onvifDeviceChannel onvif设备通道
     * @return onvif设备通道
     */
    @Override
    public List<OnvifDeviceChannelVO> listOnvifDeviceChannelVO(OnvifDeviceChannel onvifDeviceChannel) {
        LambdaQueryWrapper<CommonChannel> lqw = buildCommonChannelQueryWrapper(onvifDeviceChannel);
        List<OnvifDeviceChannel> onvifDeviceChannelList = commonChannelService.list(lqw).stream()
                .map(this::toOnvifDeviceChannel)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return OnvifDeviceChannelConvert.INSTANCE.convertOnvifDeviceChannelVOList(onvifDeviceChannelList);
    }

    private LambdaQueryWrapper<CommonChannel> buildCommonChannelQueryWrapper(OnvifDeviceChannel query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<CommonChannel> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommonChannel::getDataType, ChannelStreamType.ONVIF);
        lqw.eq(query.getId() != null, CommonChannel::getId, query.getId());
        lqw.eq(query.getDeviceId() != null, CommonChannel::getDeviceId, String.valueOf(query.getDeviceId()));
        lqw.like(StringUtils.isNotBlank(query.getName()), CommonChannel::getChannelName, query.getName());
        lqw.eq(query.getStatus() != null, CommonChannel::getStatus, toCommonStatus(query.getStatus()));
        lqw.eq(query.getEnableAudio() != null, CommonChannel::getHasAudio, query.getEnableAudio().intValue());
        lqw.eq(StringUtils.isNotBlank(query.getManufacture()), CommonChannel::getManufacture, query.getManufacture());
        lqw.eq(StringUtils.isNotBlank(query.getModel()), CommonChannel::getModel, query.getModel());
        lqw.eq(StringUtils.isNotBlank(query.getIp()), CommonChannel::getIpAddress, query.getIp());
        lqw.eq(query.getPort() != null, CommonChannel::getPort, query.getPort());
        lqw.eq(StringUtils.isNotBlank(query.getPassword()), CommonChannel::getPassword, query.getPassword());
        lqw.eq(StringUtils.isNotBlank(query.getOwner()), CommonChannel::getOwner, query.getOwner());
        lqw.eq(query.getLongitude() != null, CommonChannel::getLongitude, BigDecimal.valueOf(query.getLongitude()));
        lqw.eq(query.getLatitude() != null, CommonChannel::getLatitude, BigDecimal.valueOf(query.getLatitude()));
        lqw.eq(query.getPtzType() != null, CommonChannel::getPtzType, query.getPtzType());
        lqw.eq(StringUtils.isNotBlank(query.getLiveStreamTcp()), CommonChannel::getPlayUrl, query.getLiveStreamTcp());
        lqw.eq(StringUtils.isNotBlank(query.getMediaServiceUrl()), CommonChannel::getProxyUrl, query.getMediaServiceUrl());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), CommonChannel::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, CommonChannel::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), CommonChannel::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, CommonChannel::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), CommonChannel::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(CommonChannel::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增onvif设备通道
     *
     * @param add onvif设备通道
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(OnvifDeviceChannel add) {
        validEntityBeforeSave(add);
        add.setCreateTime(DateUtils.getNowDate());
        add.setUpdateTime(DateUtils.getNowDate());
        return saveCommonChannel(add);
    }

    /**
     * 修改onvif设备通道
     *
     * @param update onvif设备通道
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "OnvifDeviceChannel", key = "#update.id")
    public Boolean updateWithCache(OnvifDeviceChannel update) {
        validEntityBeforeSave(update);
        update.setUpdateTime(DateUtils.getNowDate());
        return saveCommonChannel(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(OnvifDeviceChannel entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除onvif设备通道信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "OnvifDeviceChannel", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        LambdaQueryWrapper<CommonChannel> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommonChannel::getDataType, ChannelStreamType.ONVIF);
        lqw.in(CommonChannel::getId, Arrays.asList(ids));
        return commonChannelService.remove(lqw);
    }


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/
    @Override
    public OnvifDeviceChannel getChannelByIpAndPort(String ip, Integer port) {
        CommonChannel commonChannel = getCommonChannelByIpAndPort(ip, port);
        return toOnvifDeviceChannel(commonChannel);
    }

    @Override
    public Boolean updateByIpAndPort(OnvifDeviceChannel update) {
        CommonChannel commonChannel = getCommonChannelByIpAndPort(update.getIp(), update.getPort());
        if (commonChannel != null) {
            update.setId(Math.toIntExact(commonChannel.getId()));
        }
        return saveCommonChannel(update);
    }

    @Override
    public Boolean clearByDeviceId(Integer deviceId) {
        LambdaQueryWrapper<CommonChannel> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommonChannel::getDataType, ChannelStreamType.ONVIF);
        lqw.eq(deviceId != null, CommonChannel::getDeviceId, String.valueOf(deviceId));
        List<Long> ids = commonChannelService.list(lqw).stream().map(CommonChannel::getId).collect(Collectors.toList());
        return commonChannelService.removeByIds(ids);
    }

    private Boolean saveCommonChannel(OnvifDeviceChannel channel) {
        CommonChannel oldChannel = getOldCommonChannel(channel);
        CommonChannel commonChannel = toCommonChannel(channel, oldChannel);
        Boolean saved = commonChannelService.upsertWithCache(commonChannel);
        if (saved && commonChannel.getId() != null) {
            channel.setId(Math.toIntExact(commonChannel.getId()));
        }
        return saved;
    }

    private CommonChannel getOldCommonChannel(OnvifDeviceChannel channel) {
        if (channel == null) {
            return null;
        }
        if (channel.getId() != null) {
            CommonChannel commonChannel = commonChannelService.getById(Long.valueOf(channel.getId()));
            if (commonChannel != null && Objects.equals(commonChannel.getDataType(), ChannelStreamType.ONVIF)) {
                return commonChannel;
            }
        }
        return getCommonChannelByIpAndPort(channel.getIp(), channel.getPort());
    }

    private CommonChannel getCommonChannelByIpAndPort(String ip, Integer port) {
        LambdaQueryWrapper<CommonChannel> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommonChannel::getDataType, ChannelStreamType.ONVIF);
        lqw.eq(StringUtils.isNotBlank(ip), CommonChannel::getIpAddress, ip);
        lqw.eq(port != null, CommonChannel::getPort, port);
        lqw.last("limit 1");
        return commonChannelService.getOne(lqw);
    }

    private CommonChannel toCommonChannel(OnvifDeviceChannel channel, CommonChannel oldChannel) {
        CommonChannel commonChannel = oldChannel == null ? new CommonChannel() : oldChannel;
        if (channel.getDeviceId() != null) {
            commonChannel.setDeviceId(String.valueOf(channel.getDeviceId()));
        }
        if (StringUtils.isBlank(commonChannel.getChannelId())) {
            commonChannel.setChannelId(buildCommonChannelId(channel));
        }
        commonChannel.setChannelName(channel.getName());
        commonChannel.setRegisterTime(commonChannel.getRegisterTime() == null ? DateUtils.getNowDate() : commonChannel.getRegisterTime());
        commonChannel.setDeviceType("ONVIF");
        commonChannel.setChannelType("ONVIF");
        commonChannel.setDataType(ChannelStreamType.ONVIF);
        commonChannel.setManufacture(channel.getManufacture());
        commonChannel.setModel(channel.getModel());
        commonChannel.setOwner(channel.getOwner());
        commonChannel.setPassword(channel.getPassword());
        commonChannel.setIpAddress(channel.getIp());
        commonChannel.setPort(channel.getPort());
        commonChannel.setPtzType(channel.getPtzType());
        commonChannel.setPtzTypeText(isSupportPtz(channel) ? "ONVIF PTZ" : "None");
        commonChannel.setStatus(toCommonStatus(channel.getStatus()));
        commonChannel.setLongitude(channel.getLongitude() == null ? null : BigDecimal.valueOf(channel.getLongitude()));
        commonChannel.setLatitude(channel.getLatitude() == null ? null : BigDecimal.valueOf(channel.getLatitude()));
        commonChannel.setStreamId(commonChannel.getChannelId());
        commonChannel.setPlayUrl(channel.getLiveStreamTcp());
        commonChannel.setProxyUrl(StringUtils.isNotBlank(channel.getMediaServiceUrl()) ? channel.getMediaServiceUrl() : channel.getLiveStreamTcp());
        commonChannel.setHasAudio(channel.getEnableAudio() == null ? null : channel.getEnableAudio().intValue());
        commonChannel.setHasBroadcast(0L);
        commonChannel.setCtrlConfig(buildCtrlConfig(channel));
        commonChannel.setExtendInfo(buildExtendInfo(channel));
        commonChannel.setDelFlag(StringUtils.isBlank(commonChannel.getDelFlag()) ? "0" : commonChannel.getDelFlag());
        commonChannel.setCreateBy(channel.getCreateBy());
        commonChannel.setUpdateBy(channel.getUpdateBy());
        commonChannel.setRemark(channel.getRemark());
        return commonChannel;
    }

    private OnvifDeviceChannel toOnvifDeviceChannel(CommonChannel commonChannel) {
        if (commonChannel == null || !Objects.equals(commonChannel.getDataType(), ChannelStreamType.ONVIF)) {
            return null;
        }
        JSONObject extendInfo = parseExtendInfo(commonChannel.getExtendInfo());
        OnvifDeviceChannel channel = new OnvifDeviceChannel();
        channel.setId(Math.toIntExact(commonChannel.getId()));
        channel.setDeviceId(parseInteger(commonChannel.getDeviceId()));
        channel.setName(commonChannel.getChannelName());
        channel.setSerialNumber(getString(extendInfo, "serialNumber", commonChannel.getDeviceId()));
        channel.setFirmwareVersion(extendInfo.getString("firmwareVersion"));
        channel.setHardwareId(extendInfo.getString("hardwareId"));
        channel.setStatus(toOnvifStatus(commonChannel.getStatus()));
        channel.setEnableAudio(commonChannel.getHasAudio() == null ? null : commonChannel.getHasAudio().longValue());
        channel.setEnableMp4(extendInfo.getLong("enableMp4"));
        channel.setManufacture(commonChannel.getManufacture());
        channel.setModel(commonChannel.getModel());
        channel.setIp(commonChannel.getIpAddress());
        channel.setPort(commonChannel.getPort());
        channel.setUsername(extendInfo.getString("username"));
        channel.setPassword(commonChannel.getPassword());
        channel.setOwner(commonChannel.getOwner());
        channel.setAddress(extendInfo.getString("address"));
        channel.setLongitude(commonChannel.getLongitude() == null ? null : commonChannel.getLongitude().longValue());
        channel.setLatitude(commonChannel.getLatitude() == null ? null : commonChannel.getLatitude().longValue());
        channel.setPtzType(commonChannel.getPtzType());
        channel.setPositionType(extendInfo.getLong("positionType"));
        channel.setRoomType(extendInfo.getLong("roomType"));
        channel.setUseType(extendInfo.getLong("useType"));
        channel.setSupplylightType(extendInfo.getLong("supplylightType"));
        channel.setDirectionType(extendInfo.getLong("directionType"));
        channel.setLiveStreamTcp(commonChannel.getPlayUrl());
        channel.setLiveStreamUdp(extendInfo.getString("liveStreamUdp"));
        channel.setLiveStreamMulticast(extendInfo.getString("liveStreamMulticast"));
        channel.setReplayStream(extendInfo.getString("replayStream"));
        channel.setProfileToken(extendInfo.getString("profileToken"));
        channel.setMediaServiceUrl(getString(extendInfo, "mediaServiceUrl", commonChannel.getProxyUrl()));
        channel.setPtzServiceUrl(extendInfo.getString("ptzServiceUrl"));
        channel.setSnapshotUri(extendInfo.getString("snapshotUri"));
        channel.setSupportPtz(extendInfo.getInteger("supportPtz"));
        channel.setEncoding(extendInfo.getString("encoding"));
        channel.setResolutionWidth(extendInfo.getInteger("resolutionWidth"));
        channel.setResolutionHeight(extendInfo.getInteger("resolutionHeight"));
        channel.setCreateBy(commonChannel.getCreateBy());
        channel.setCreateTime(commonChannel.getCreateTime());
        channel.setUpdateBy(commonChannel.getUpdateBy());
        channel.setUpdateTime(commonChannel.getUpdateTime());
        channel.setRemark(commonChannel.getRemark());
        return channel;
    }

    private String buildCommonChannelId(OnvifDeviceChannel channel) {
        if (StringUtils.isNotBlank(channel.getIp()) && channel.getPort() != null) {
            return ONVIF_CHANNEL_PREFIX + channel.getIp() + "-" + channel.getPort();
        }
        if (StringUtils.isNotBlank(channel.getSerialNumber())) {
            return ONVIF_CHANNEL_PREFIX + channel.getSerialNumber();
        }
        return channel.getId() == null ? ONVIF_CHANNEL_PREFIX + System.currentTimeMillis() : String.valueOf(channel.getId());
    }

    private String buildCtrlConfig(OnvifDeviceChannel channel) {
        JSONObject config = new JSONObject();
        config.put("profileToken", channel.getProfileToken());
        config.put("supportPtz", isSupportPtz(channel) ? 1 : 0);
        return JSON.toJSONString(config);
    }

    private String buildExtendInfo(OnvifDeviceChannel channel) {
        JSONObject extendInfo = new JSONObject();
        extendInfo.put("protocol", "ONVIF");
        extendInfo.put("onvifDeviceId", channel.getDeviceId());
        extendInfo.put("serialNumber", channel.getSerialNumber());
        extendInfo.put("firmwareVersion", channel.getFirmwareVersion());
        extendInfo.put("hardwareId", channel.getHardwareId());
        extendInfo.put("username", channel.getUsername());
        extendInfo.put("address", channel.getAddress());
        extendInfo.put("enableMp4", channel.getEnableMp4());
        extendInfo.put("positionType", channel.getPositionType());
        extendInfo.put("roomType", channel.getRoomType());
        extendInfo.put("useType", channel.getUseType());
        extendInfo.put("supplylightType", channel.getSupplylightType());
        extendInfo.put("directionType", channel.getDirectionType());
        extendInfo.put("liveStreamUdp", channel.getLiveStreamUdp());
        extendInfo.put("liveStreamMulticast", channel.getLiveStreamMulticast());
        extendInfo.put("replayStream", channel.getReplayStream());
        extendInfo.put("profileToken", channel.getProfileToken());
        extendInfo.put("mediaServiceUrl", channel.getMediaServiceUrl());
        extendInfo.put("ptzServiceUrl", channel.getPtzServiceUrl());
        extendInfo.put("snapshotUri", channel.getSnapshotUri());
        extendInfo.put("supportPtz", channel.getSupportPtz());
        extendInfo.put("encoding", channel.getEncoding());
        extendInfo.put("resolutionWidth", channel.getResolutionWidth());
        extendInfo.put("resolutionHeight", channel.getResolutionHeight());
        return JSON.toJSONString(extendInfo);
    }

    private JSONObject parseExtendInfo(String extendInfo) {
        if (StringUtils.isBlank(extendInfo)) {
            return new JSONObject();
        }
        try {
            return JSON.parseObject(extendInfo);
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    private String getString(JSONObject json, String key, String defaultValue) {
        String value = json.getString(key);
        return StringUtils.isNotBlank(value) ? value : defaultValue;
    }

    private Integer parseInteger(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer toCommonStatus(Long status) {
        if (status == null) {
            return null;
        }
        if (status == 1L) {
            return 3;
        }
        if (status == 0L) {
            return 4;
        }
        return status.intValue();
    }

    private Long toOnvifStatus(Integer status) {
        if (status == null) {
            return null;
        }
        if (status == 3) {
            return 1L;
        }
        if (status == 4) {
            return 0L;
        }
        return status.longValue();
    }

    private boolean isSupportPtz(OnvifDeviceChannel channel) {
        return channel.getSupportPtz() != null && channel.getSupportPtz().intValue() == 1;
    }

    /** 自定义代码区域 END**/
}
