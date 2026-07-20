package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.model.enums.AiQueryMode;
import com.fastbee.ai.model.vo.AiQueryGovernanceSnapshotVO;
import com.fastbee.ai.model.vo.AiQueryRouteVO;
import com.fastbee.ai.model.vo.AiRedisRealtimeQueryResultVO;
import com.fastbee.ai.model.vo.AiSemanticContextVO;
import com.fastbee.ai.model.vo.AiSemanticFieldVO;
import com.fastbee.ai.service.IAiDeviceResolveService;
import com.fastbee.ai.service.IAiQueryGovernanceService;
import com.fastbee.ai.service.IAiQuerySourceRouter;
import com.fastbee.ai.service.IAiRedisRealtimeQueryService;
import com.fastbee.ai.service.IAiSemanticNormalizationService;
import com.fastbee.ai.support.AiRuntimeFieldSelectionSupport;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.cache.ITSLValueCache;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.ThingsModelItem.Datatype;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.iot.model.ThingsModels.ValueItem;
import com.fastbee.iot.service.IThingsModelService;

@Service
public class AiRedisRealtimeQueryServiceImpl implements IAiRedisRealtimeQueryService {

    @Resource
    private IAiSemanticNormalizationService aiSemanticNormalizationService;

    @Resource
    private IAiQuerySourceRouter aiQuerySourceRouter;

    @Resource
    private IAiDeviceResolveService aiDeviceResolveService;

    @Resource
    private IAiQueryGovernanceService aiQueryGovernanceService;

    @Resource
    private ITSLValueCache itslValueCache;

    @Resource
    private IThingsModelService thingsModelService;

    @Override
    public AiRedisRealtimeQueryResultVO queryCurrentValue(String question) {
        AiSemanticContextVO context = aiSemanticNormalizationService.buildNl2SqlContext(question);
        AiQueryRouteVO route = aiQuerySourceRouter.route(question, context);
        if (!AiQueryMode.REDIS_VALUE.name().equals(route.getQueryMode())) {
            throw new ServiceException(message("ai.redis.query.route.required"));
        }

        DeviceMetaData deviceMetaData = aiDeviceResolveService.resolveRequiredDeviceMetaData(question);
        Device device = deviceMetaData.getDevice();
        String serialNumber = device.getSerialNumber();

        AiSemanticFieldVO runtimeField = resolveRuntimeField(question, context);
        String identifier = runtimeField.getSourceCode();
        AiQueryGovernanceSnapshotVO governanceSnapshot =
                aiQueryGovernanceService.validateMultiSourceAccess(deviceMetaData, identifier, AiQueryMode.REDIS_VALUE.name());
        ValueItem valueItem = itslValueCache.getCacheIdentifier(device.getProductId(), serialNumber, identifier);
        if (valueItem == null) {
            throw new ServiceException(message("ai.redis.query.current.value.not.found"));
        }

        AiRedisRealtimeQueryResultVO result = new AiRedisRealtimeQueryResultVO();
        result.setQuestion(question);
        result.setQueryMode(AiQueryMode.REDIS_VALUE.name());
        result.setDeviceId(governanceSnapshot.getDeviceId());
        result.setSerialNumber(serialNumber);
        result.setDeviceName(device.getDeviceName());
        result.setProductId(device.getProductId());
        result.setProductName(governanceSnapshot.getProductName());
        result.setSemanticName(runtimeField.getSemanticName());
        result.setIdentifier(identifier);
        result.setCurrentValue(valueItem.getValue());
        result.setDisplayName(StringUtils.isNotBlank(valueItem.getName()) ? valueItem.getName() : runtimeField.getSemanticName());
        result.setUnit(resolveUnit(device.getProductId(), identifier));
        result.setReportTime(formatDate(valueItem.getTs()));
        result.setCacheHit(Boolean.TRUE);
        result.setAccessValidated(governanceSnapshot.getAccessValidated());
        result.getPermissionChecks().addAll(governanceSnapshot.getPermissionChecks());
        result.getMessages().add(route.getRouteReason());
        result.getMessages().addAll(governanceSnapshot.getPermissionChecks());
        result.getMessages().add("设备与产品信息来自设备元数据缓存。");
        result.getMessages().add("当前首版支持通过设备编号或设备名称解析设备，再结合运行时物模型语义读取 Redis 实时值。");
        result.setSummary(buildSummary(result));
        return result;
    }

    private AiSemanticFieldVO resolveRuntimeField(String question, AiSemanticContextVO context) {
        List<AiSemanticFieldVO> runtimeFields = context.getRuntimeFields();
        return AiRuntimeFieldSelectionSupport.resolveRuntimeField(question, runtimeFields,
                "未命中可用的运行时物模型语义，无法执行实时值查询。", "实时值查询");
    }

    private String resolveUnit(Long productId, String identifier) {
        try {
            ThingsModelValueItem item = thingsModelService.getSingleThingModels(productId, identifier);
            if (item == null) {
                return null;
            }
            Datatype datatype = item.getDatatype();
            return datatype == null ? null : datatype.getUnit();
        } catch (Exception ignored) {
            return null;
        }
    }

    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(date);
    }

    private String buildSummary(AiRedisRealtimeQueryResultVO result) {
        StringBuilder builder = new StringBuilder();
        builder.append("设备 ").append(result.getSerialNumber()).append(" 的 ");
        builder.append(StringUtils.isNotBlank(result.getSemanticName()) ? result.getSemanticName() : result.getDisplayName());
        builder.append(" 当前值为 ").append(StringUtils.isNotBlank(result.getCurrentValue()) ? result.getCurrentValue() : "-");
        if (StringUtils.isNotBlank(result.getUnit())) {
            builder.append(result.getUnit());
        }
        if (StringUtils.isNotBlank(result.getReportTime())) {
            builder.append("，上报时间 ").append(result.getReportTime());
        }
        return builder.toString();
    }
}
