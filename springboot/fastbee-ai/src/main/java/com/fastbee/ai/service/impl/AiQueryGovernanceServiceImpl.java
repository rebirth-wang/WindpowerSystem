package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import org.springframework.stereotype.Service;

import com.fastbee.ai.model.vo.AiQueryGovernanceSnapshotVO;
import com.fastbee.ai.service.IAiQueryGovernanceService;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.model.DeviceMetaData;

/**
 * AI 问数治理服务实现。
 */
@Service
public class AiQueryGovernanceServiceImpl implements IAiQueryGovernanceService {

    @Override
    public AiQueryGovernanceSnapshotVO validateMultiSourceAccess(DeviceMetaData deviceMetaData, String identifier, String queryMode) {
        Device device = requireDevice(deviceMetaData);
        Product product = requireProduct(deviceMetaData, device);

        AiQueryGovernanceSnapshotVO snapshot = new AiQueryGovernanceSnapshotVO();
        snapshot.setQueryMode(queryMode);
        snapshot.setDeviceId(device.getDeviceId());
        snapshot.setSerialNumber(device.getSerialNumber());
        snapshot.setDeviceName(device.getDeviceName());
        snapshot.setProductId(product.getProductId());
        snapshot.setProductName(product.getProductName());
        snapshot.setIdentifier(StringUtils.trim(identifier));

        snapshot.getPermissionChecks().add("设备访问已在设备解析阶段完成校验");
        snapshot.getPermissionChecks().add("产品归属随设备所属产品确认");
        snapshot.getPermissionChecks().add(StringUtils.isBlank(identifier)
                ? "当前问题未命中具体物模型标识，跳过物模型一致性说明"
                : "运行时物模型按设备所属产品直接读取，不额外校验物模型分享权限");
        snapshot.setAccessValidated(Boolean.TRUE);
        return snapshot;
    }

    private Device requireDevice(DeviceMetaData deviceMetaData) {
        if (deviceMetaData == null || deviceMetaData.getDevice() == null || deviceMetaData.getDevice().getDeviceId() == null) {
            throw new ServiceException(message("ai.query.governance.device.required"));
        }
        return deviceMetaData.getDevice();
    }

    private Product requireProduct(DeviceMetaData deviceMetaData, Device device) {
        Product product = deviceMetaData == null ? null : deviceMetaData.getProduct();
        if (product == null || product.getProductId() == null) {
            throw new ServiceException(message("ai.query.governance.product.required"));
        }
        if (device.getProductId() != null && !device.getProductId().equals(product.getProductId())) {
            throw new ServiceException(message("ai.query.governance.product.mismatch"));
        }
        return product;
    }
}
