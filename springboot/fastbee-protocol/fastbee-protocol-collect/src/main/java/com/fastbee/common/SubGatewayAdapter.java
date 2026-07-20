package com.fastbee.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fastbee.common.extend.core.domin.mq.DeviceReport;
import com.fastbee.common.extend.core.domin.mq.SubDeviceBo;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.domain.SubGateway;
import com.fastbee.iot.enums.DeviceType;
import com.fastbee.iot.model.DeviceMetaData;

/**
 * @author zzy
 * @description: Modbus匹配子设备适配器
 * @date 2025-10-29 15:26
 */
@Component
public class SubGatewayAdapter {

    @Resource
    private IDeviceCache deviceCache;

    /**
     * 匹配子设备编号
     */
    public void matchSubDev(DeviceReport report, Integer address) {
        DeviceMetaData deviceMetaDataCache = deviceCache.getDeviceMetaDataCache(report.getSerialNumber());
        Integer deviceType = deviceMetaDataCache.getProduct().getDeviceType();
        if (DeviceType.DIRECT_DEVICE.getCode() == deviceType) {
            // 直连设备区分不同从机
            report.setDirectConnAddress(address.toString());
        } else if (DeviceType.GATEWAY.getCode() == deviceType) {
            List<SubGateway> subDeviceList = deviceMetaDataCache.getSubGatewayList();
            if (!CollectionUtils.isEmpty(subDeviceList)) {
                SubGateway subGateway = subDeviceList.stream().filter(sub -> sub.getAddress().equals(address.toString())).findFirst().orElse(null);
                if (Objects.nonNull(subGateway)) {
                    report.setSubDeviceBoList(this.getSubDeviceBoList(address, subGateway));
//                report.setSerialNumber(subGateway.getSubClientId());
//                report.setProductId(subGateway.getSubProductId());
                }
            }
        }
    }

    private List<SubDeviceBo> getSubDeviceBoList(Integer address, SubGateway subGateway) {
        List<SubDeviceBo> subDeviceBoList = new ArrayList<>();
        SubDeviceBo subDeviceBo = new SubDeviceBo();
        subDeviceBo.setAddress(address.toString());
        subDeviceBo.setSubClientId(subGateway.getSubClientId());
        subDeviceBo.setSubProductId(subGateway.getSubProductId());
        subDeviceBoList.add(subDeviceBo);
        return subDeviceBoList;
    }
}
