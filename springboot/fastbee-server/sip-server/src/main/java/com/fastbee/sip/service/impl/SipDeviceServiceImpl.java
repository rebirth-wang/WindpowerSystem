package com.fastbee.sip.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.service.ICommonChannelService;
import com.fastbee.sip.convert.SipDeviceConvert;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.mapper.SipDeviceMapper;
import com.fastbee.sip.model.BaseTree;
import com.fastbee.sip.model.vo.SipDeviceVO;
import com.fastbee.sip.service.ISipChannelService;
import com.fastbee.sip.service.ISipDeviceService;

/**
 * 监控设备Service业务层处理
 *
 * @author zhuangpeng.li
 * @date 2022-10-07
 */
@Service
@Slf4j
public class SipDeviceServiceImpl extends ServiceImpl<SipDeviceMapper, SipDevice> implements ISipDeviceService {
    @Resource
    private SipDeviceMapper sipDeviceMapper;

    @Resource
    private ICommonChannelService commonChannelService;

    @Resource
    private ISipChannelService sipChannelService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private IDeviceService deviceService;

    @Override
    public boolean exists(String sipId) {
        return sipDeviceMapper.selectSipDeviceBySipId(sipId) != null;
    }

    @Override
    public boolean updateDevice(SipDevice device) {
        SipDevice devicetemp = this.selectSipDeviceBySipId(device.getDeviceSipId());
        if (devicetemp == null) {
            return sipDeviceMapper.insert(device) > 0;
        } else {
            device.setDeviceId(devicetemp.getDeviceId());
            return sipDeviceMapper.updateById(device) > 0;
        }
    }

    /**
     * 查询监控设备
     *
     * @param deviceId 监控设备主键
     * @return 监控设备
     */
    @Override
    public SipDevice selectSipDeviceByDeviceId(Long deviceId) {
        SipDevice sipDevice = new SipDevice();
        sipDevice.setDeviceId(deviceId);
        return sipDeviceMapper.selectOne(buildQueryWrapper(sipDevice));
    }

    @Override
    public SipDevice selectSipDeviceBySipId(String sipId) {
        SipDevice sipDevice = new SipDevice();
        sipDevice.setDeviceSipId(sipId);
        return sipDeviceMapper.selectOne(buildQueryWrapper(sipDevice));
    }

    /**
     * 查询监控设备列表
     *
     * @param sipDevice 监控设备
     * @return 监控设备
     */
    @Override
    public List<SipDeviceVO> selectSipDeviceList(SipDevice sipDevice) {
        LambdaQueryWrapper<SipDevice> lqw = buildQueryWrapper(sipDevice);
        List<SipDevice> sipDeviceList = baseMapper.selectList(lqw);
        return SipDeviceConvert.INSTANCE.convertSipDeviceVOList(sipDeviceList);
    }

    /**
     * 查询监控设备分页列表
     *
     * @param sipDevice 监控设备
     * @return 监控设备
     */
    @Override
    public Page<SipDeviceVO> pageSipDeviceVO(SipDevice sipDevice) {
        LambdaQueryWrapper<SipDevice> lqw = buildQueryWrapper(sipDevice);
        Page<SipDevice> sipDevicePage = baseMapper.selectPage(new Page<>(sipDevice.getPageNum(), sipDevice.getPageSize()), lqw);
        return SipDeviceConvert.INSTANCE.convertSipDeviceVOPage(sipDevicePage);
    }

    private LambdaQueryWrapper<SipDevice> buildQueryWrapper(SipDevice query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SipDevice> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getProductId() != null, SipDevice::getProductId, query.getProductId());
        lqw.like(StringUtils.isNotBlank(query.getProductName()), SipDevice::getProductName, query.getProductName());
        lqw.eq(StringUtils.isNotBlank(query.getDeviceSipId()), SipDevice::getDeviceSipId, query.getDeviceSipId());
        lqw.like(StringUtils.isNotBlank(query.getDeviceName()), SipDevice::getDeviceName, query.getDeviceName());
        lqw.eq(StringUtils.isNotBlank(query.getManufacturer()), SipDevice::getManufacturer, query.getManufacturer());
        lqw.eq(StringUtils.isNotBlank(query.getModel()), SipDevice::getModel, query.getModel());
        lqw.eq(StringUtils.isNotBlank(query.getFirmware()), SipDevice::getFirmware, query.getFirmware());
        lqw.eq(StringUtils.isNotBlank(query.getTransport()), SipDevice::getTransport, query.getTransport());
        lqw.eq(StringUtils.isNotBlank(query.getStreamMode()), SipDevice::getStreamMode, query.getStreamMode());
        lqw.eq(StringUtils.isNotBlank(query.getOnlineStatus()), SipDevice::getOnlineStatus, query.getOnlineStatus());
        lqw.eq(query.getRegisterTime() != null, SipDevice::getRegisterTime, query.getRegisterTime());
        lqw.eq(query.getLastConnectTime() != null, SipDevice::getLastConnectTime, query.getLastConnectTime());
        lqw.eq(query.getActiveTime() != null, SipDevice::getActiveTime, query.getActiveTime());
        lqw.eq(StringUtils.isNotBlank(query.getIp()), SipDevice::getIp, query.getIp());
        lqw.eq(query.getPort() != null, SipDevice::getPort, query.getPort());
        lqw.eq(StringUtils.isNotBlank(query.getHostAddress()), SipDevice::getHostAddress, query.getHostAddress());
        lqw.eq(StringUtils.isNotBlank(query.getServerId()), SipDevice::getServerId, query.getServerId());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SipDevice::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    @Override
    public List<BaseTree<CommonChannel>> selectSipDeviceChannelList(String deviceId) {
        return sipChannelService.queryVideoDeviceTree(deviceId, null, false);
    }


    @Override
    public int updateSipDeviceStatus(SipDevice sipDevice) {
        sipDevice.setUpdateTime(DateUtils.getNowDate());
        return sipDeviceMapper.updateSipDeviceStatus(sipDevice);
    }

    /**
     * 批量删除监控设备
     *
     * @param deviceIds 需要删除的监控设备主键
     * @return 结果
     */
    @Override
    public int deleteSipDeviceByDeviceIds(Long[] deviceIds) {
        return sipDeviceMapper.deleteBatchIds(Arrays.asList(deviceIds));
    }

    /**
     * 删除监控设备信息
     *
     * @param deviceId 监控设备主键
     * @return 结果
     */
    @Override
    public int deleteSipDeviceByDeviceId(String deviceId) {
        return sipDeviceMapper.deleteById(Long.valueOf(deviceId));
    }

    @Override
    public int deleteSipDeviceBySipId(String SipId) {
        Device dev = deviceService.selectDeviceBySerialNumber(SipId);
        try {
            int ret = deviceService.deleteDeviceByDeviceId(dev.getDeviceId());
            if (ret == 0) {
                String key;
                sipDeviceMapper.deleteSipDeviceByByDeviceSipId(SipId);
                key = RedisKeyBuilder.buildSipDeviceidCacheKey(SipId.substring(0, 14));
                redisCache.setRemove(key, Integer.valueOf(SipId.substring(SipId.length() - 6)));
                List<CommonChannel> list = commonChannelService.lambdaQuery().eq(CommonChannel::getDeviceId, SipId).list();
                for (CommonChannel sipDeviceChannel : list) {
                    String channelId = sipDeviceChannel.getChannelId();
                    key = RedisKeyBuilder.buildSipChannelidCacheKey(channelId.substring(0, 14));
                    redisCache.setRemove(key, Integer.valueOf(channelId.substring(channelId.length() - 6)));
                }
                LambdaQueryWrapper<CommonChannel> lqw = Wrappers.lambdaQuery();
                lqw.eq(CommonChannel::getDeviceId, SipId);
                return ret + (commonChannelService.remove(lqw) ? 1 : 0);
            } else {
                return ret;
            }
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        }
        return 0;
    }
}
