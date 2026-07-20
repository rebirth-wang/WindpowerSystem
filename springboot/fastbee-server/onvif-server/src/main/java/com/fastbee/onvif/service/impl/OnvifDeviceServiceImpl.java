package com.fastbee.onvif.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.onvif.domain.OnvifDevice;
import com.fastbee.onvif.domain.vo.OnvifDeviceVO;
import com.fastbee.onvif.service.IOnvifDeviceService;

/**
 * ONVIF 节点信息服务。
 *
 * <p>ONVIF 节点不再单独写入 onvif_device 表，主键使用 iot_device.device_id。
 * status、ip、lastHeartbeat 分别映射到 iot_device.status、network_ip、active_time，
 * 仅 ONVIF 专属扩展信息维护在 iot_device.summary.onvif 中。</p>
 *
 * @author fastbee
 */
@Service
public class OnvifDeviceServiceImpl implements IOnvifDeviceService {

    private static final String ONVIF_SUMMARY_KEY = "onvif";
    private static final String LEGACY_SUMMARY_KEY = "legacySummary";
    private static final int IOT_STATUS_ONLINE = 3;
    private static final int IOT_STATUS_OFFLINE = 4;

    private final IDeviceService deviceService;

    public OnvifDeviceServiceImpl(IDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /** 代码生成区域 可直接覆盖**/
    /**
     * 查询onvif设备
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键，对应 iot_device.device_id
     * @return onvif设备
     */
    @Override
    @Cacheable(cacheNames = "OnvifDevice", key = "#id")
    public OnvifDevice queryByIdWithCache(Integer id){
        return selectOnvifDeviceById(id);
    }

    /**
     * 查询onvif设备
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键，对应 iot_device.device_id
     * @return onvif设备
     */
    @Override
    @Cacheable(cacheNames = "OnvifDevice", key = "#id")
    public OnvifDevice selectOnvifDeviceById(Integer id){
        if (id == null) {
            return null;
        }
        Device device = deviceService.getById(id.longValue());
        return toOnvifDevice(device);
    }

    /**
     * 查询onvif设备分页列表
     *
     * @param onvifDevice onvif设备
     * @return onvif设备
     */
    @Override
    public Page<OnvifDeviceVO> pageOnvifDeviceVO(OnvifDevice onvifDevice) {
        Page<Device> page = deviceService.page(
                new Page<>(onvifDevice.getPageNum(), onvifDevice.getPageSize()),
                buildDeviceQueryWrapper(onvifDevice));
        List<OnvifDeviceVO> records = page.getRecords().stream()
                .map(this::toOnvifDevice)
                .filter(Objects::nonNull)
                .map(this::toOnvifDeviceVO)
                .collect(Collectors.toList());
        Page<OnvifDeviceVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(records);
        return result;
    }

    /**
     * 查询onvif设备列表
     *
     * @param onvifDevice onvif设备
     * @return onvif设备
     */
    @Override
    public List<OnvifDeviceVO> listOnvifDeviceVO(OnvifDevice onvifDevice) {
        return deviceService.list(buildDeviceQueryWrapper(onvifDevice)).stream()
                .map(this::toOnvifDevice)
                .filter(Objects::nonNull)
                .map(this::toOnvifDeviceVO)
                .collect(Collectors.toList());
    }

    private LambdaQueryWrapper<Device> buildDeviceQueryWrapper(OnvifDevice query) {
        LambdaQueryWrapper<Device> lqw = Wrappers.lambdaQuery();
        if (query == null) {
            return lqw.like(Device::getSummary, "\"" + ONVIF_SUMMARY_KEY + "\"");
        }
        Map<String, Object> params = query.getParams();
        lqw.eq(query.getId() != null, Device::getDeviceId, query.getId());
        lqw.like(StringUtils.isNotBlank(query.getName()), Device::getDeviceName, query.getName());
        if (query.getStatus() != null) {
            lqw.eq(Device::getStatus, query.getStatus() == 1L ? IOT_STATUS_ONLINE : IOT_STATUS_OFFLINE);
        }
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), Device::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, Device::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), Device::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, Device::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), Device::getRemark, query.getRemark());
        lqw.like(Device::getSummary, "\"" + ONVIF_SUMMARY_KEY + "\"");

        if (params != null && !Objects.isNull(params.get("beginTime"))
                && !Objects.isNull(params.get("endTime"))) {
            lqw.between(Device::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增onvif设备
     *
     * @param add onvif设备
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(OnvifDevice add) {
        if (add == null || add.getId() == null) {
            return false;
        }
        Device device = deviceService.getById(add.getId().longValue());
        if (device == null) {
            return false;
        }
        mergeToDevice(device, add);
        if (device.getCreateTime() == null) {
            device.setCreateTime(DateUtils.getNowDate());
        }
        device.setUpdateTime(DateUtils.getNowDate());
        return deviceService.updateById(device);
    }

    /**
     * 修改onvif设备
     *
     * @param update onvif设备
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "OnvifDevice", key = "#update.id")
    public Boolean updateWithCache(OnvifDevice update) {
        if (update == null || update.getId() == null) {
            return false;
        }
        Device device = deviceService.getById(update.getId().longValue());
        if (device == null) {
            return false;
        }
        mergeToDevice(device, update);
        device.setUpdateTime(DateUtils.getNowDate());
        return deviceService.updateById(device);
    }

    /**
     * 校验并批量删除onvif设备信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "OnvifDevice", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if (ids == null || ids.length == 0) {
            return true;
        }
        List<Device> devices = deviceService.listByIds(Arrays.asList(ids));
        for (Device device : devices) {
            JSONObject summary = parseSummary(device.getSummary());
            summary.remove(ONVIF_SUMMARY_KEY);
            device.setSummary(summary.toJSONString());
            device.setUpdateTime(DateUtils.getNowDate());
            deviceService.updateById(device);
        }
        return true;
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/

    private OnvifDevice toOnvifDevice(Device device) {
        if (device == null) {
            return null;
        }
        JSONObject onvif = parseSummary(device.getSummary()).getJSONObject(ONVIF_SUMMARY_KEY);
        OnvifDevice onvifDevice = new OnvifDevice();
        onvifDevice.setId(Math.toIntExact(device.getDeviceId()));
        onvifDevice.setName(device.getDeviceName());
        onvifDevice.setStatus(toOnvifStatus(device.getStatus()));
        onvifDevice.setDirectConnection(onvif != null ? onvif.getLong("directConnection") : 1L);
        onvifDevice.setIp(device.getNetworkIp());
        onvifDevice.setLastHeartbeat(device.getActiveTime());
        onvifDevice.setCreateBy(device.getCreateBy());
        onvifDevice.setCreateTime(device.getCreateTime());
        onvifDevice.setUpdateBy(device.getUpdateBy());
        onvifDevice.setUpdateTime(device.getUpdateTime());
        onvifDevice.setRemark(device.getRemark());
        return onvifDevice;
    }

    private OnvifDeviceVO toOnvifDeviceVO(OnvifDevice device) {
        OnvifDeviceVO vo = new OnvifDeviceVO();
        vo.setId(device.getId());
        vo.setName(device.getName());
        vo.setStatus(device.getStatus());
        vo.setDirectConnection(device.getDirectConnection());
        vo.setCreateBy(device.getCreateBy());
        vo.setCreateTime(device.getCreateTime());
        vo.setUpdateBy(device.getUpdateBy());
        vo.setUpdateTime(device.getUpdateTime());
        vo.setRemark(device.getRemark());
        return vo;
    }

    private void mergeToDevice(Device device, OnvifDevice onvifDevice) {
        if (StringUtils.isNotBlank(onvifDevice.getName())) {
            device.setDeviceName(onvifDevice.getName());
        }
        if (StringUtils.isNotBlank(onvifDevice.getIp())) {
            device.setNetworkIp(onvifDevice.getIp());
        }
        if (onvifDevice.getStatus() != null) {
            device.setStatus(onvifDevice.getStatus() == 1L ? IOT_STATUS_ONLINE : IOT_STATUS_OFFLINE);
        }
        if (onvifDevice.getLastHeartbeat() != null) {
            device.setActiveTime(onvifDevice.getLastHeartbeat());
        }
        if (StringUtils.isNotBlank(onvifDevice.getRemark())) {
            device.setRemark(onvifDevice.getRemark());
        }

        JSONObject summary = parseSummary(device.getSummary());
        JSONObject onvif = summary.getJSONObject(ONVIF_SUMMARY_KEY);
        if (onvif == null) {
            onvif = new JSONObject();
        }
        onvif.remove("status");
        onvif.remove("ip");
        onvif.remove("lastHeartbeat");
        if (onvifDevice.getDirectConnection() != null) {
            onvif.put("directConnection", onvifDevice.getDirectConnection());
        }
        summary.put(ONVIF_SUMMARY_KEY, onvif);
        device.setSummary(summary.toJSONString());
    }

    private JSONObject parseSummary(String summary) {
        JSONObject result = new JSONObject();
        if (!StringUtils.isNotBlank(summary)) {
            return result;
        }
        try {
            Object parsed = JSON.parse(summary);
            if (parsed instanceof JSONObject) {
                result = (JSONObject) parsed;
            } else if (parsed instanceof JSONArray) {
                result.put(LEGACY_SUMMARY_KEY, parsed);
            } else if (parsed != null) {
                result.put(LEGACY_SUMMARY_KEY, parsed);
            }
        } catch (Exception ignored) {
            result.put(LEGACY_SUMMARY_KEY, summary);
        }
        return result;
    }

    private Long toOnvifStatus(Integer deviceStatus) {
        return deviceStatus != null && deviceStatus == IOT_STATUS_ONLINE ? 1L : 0L;
    }

    /** 自定义代码区域 END**/
}
