package com.fastbee.ai.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.domain.AiModel;
import com.fastbee.ai.domain.AiProvider;
import com.fastbee.ai.mapper.AiModelMapper;
import com.fastbee.ai.mapper.AiProviderMapper;
import com.fastbee.ai.model.enums.AiModelProviderType;
import com.fastbee.ai.model.enums.AiRegionProfile;
import com.fastbee.ai.model.vo.AiModelRouteVO;
import com.fastbee.ai.service.AiModelRoutingService;
import com.fastbee.ai.support.AiSecuritySupport;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.utils.StringUtils;

/**
 * 模型路由默认实现。
 */
@Service
public class AiModelRoutingServiceImpl implements AiModelRoutingService {

    @Resource
    private FastBeeAiProperties properties;

    @Resource
    private AiModelMapper aiModelMapper;

    @Resource
    private AiProviderMapper aiProviderMapper;

    @Override
    public AiModelRouteVO resolveDefaultRoute() {
        AiModelRouteVO databaseRoute = resolveDatabaseDefaultRoute();
        if (databaseRoute != null) {
            return databaseRoute;
        }
        return resolveByRegion(properties.getRegion());
    }

    @Override
    public AiModelRouteVO resolveByModelCode(String modelCode) {
        if (StringUtils.isBlank(modelCode)) {
            return resolveDefaultRoute();
        }
        SysUser user = AiSecuritySupport.getCurrentUser();
        Long tenantId = AiSecuritySupport.resolveTenantId(user);
        List<AiModel> modelList = aiModelMapper.selectList(Wrappers.<AiModel>lambdaQuery()
                .eq(AiModel::getModelCode, modelCode)
                .eq(AiModel::getStatus, "0")
                .eq(user != null && !user.isAdmin() && tenantId != null, AiModel::getTenantId, tenantId)
                .orderByDesc(AiModel::getIsDefault)
                .orderByDesc(AiModel::getUpdateTime));
        if (modelList.isEmpty()) {
            return resolveDefaultRoute();
        }
        AiProvider provider = aiProviderMapper.selectById(modelList.get(0).getProviderId());
        if (provider == null || !"0".equals(provider.getStatus())) {
            return resolveDefaultRoute();
        }
        return buildDatabaseRoute(modelList.get(0), provider);
    }

    @Override
    public AiModelRouteVO resolveByRegion(AiRegionProfile region) {
        AiRegionProfile targetRegion = region == null ? properties.getRegion() : region;
        Map<String, FastBeeAiProperties.ProviderGroupProperties> providerMap = properties.getProviders();
        FastBeeAiProperties.ProviderGroupProperties providerGroup =
                providerMap.get(targetRegion.name().toLowerCase(Locale.ROOT));

        AiModelRouteVO route = new AiModelRouteVO();
        route.setRegion(targetRegion);
        if (providerGroup == null) {
            route.setProviderCode(properties.getDefaultProvider());
            route.setProviderType(AiModelProviderType.fromCode(properties.getDefaultProvider()));
            return route;
        }
        route.setProviderCode(providerGroup.getProvider());
        route.setProviderType(AiModelProviderType.fromCode(providerGroup.getProvider()));
        route.setModelCode(providerGroup.getModel());
        route.setBaseUrl(providerGroup.getBaseUrl());
        return route;
    }

    private AiModelRouteVO resolveDatabaseDefaultRoute() {
        SysUser user = AiSecuritySupport.getCurrentUser();
        Long tenantId = AiSecuritySupport.resolveTenantId(user);
        List<AiModel> modelList = aiModelMapper.selectList(Wrappers.<AiModel>lambdaQuery()
                .eq(AiModel::getStatus, "0")
                .eq(AiModel::getIsDefault, "1")
                .eq(user != null && !user.isAdmin() && tenantId != null, AiModel::getTenantId, tenantId)
                .orderByDesc(AiModel::getUpdateTime));
        if (modelList.isEmpty()) {
            return null;
        }
        AiProvider provider = aiProviderMapper.selectById(modelList.get(0).getProviderId());
        if (provider == null || !"0".equals(provider.getStatus())) {
            return null;
        }
        return buildDatabaseRoute(modelList.get(0), provider);
    }

    private AiModelRouteVO buildDatabaseRoute(AiModel model, AiProvider provider) {
        AiModelRouteVO route = new AiModelRouteVO();
        route.setProviderId(provider.getProviderId());
        route.setRegion(AiRegionProfile.fromCode(provider.getRegionProfile()));
        route.setProviderCode(provider.getProviderCode());
        route.setProviderName(provider.getProviderName());
        route.setProviderType(AiModelProviderType.fromCode(provider.getProviderCode()));
        route.setModelId(model.getModelId());
        route.setModelCode(model.getModelCode());
        route.setModelName(model.getModelName());
        route.setBaseUrl(provider.getApiBaseUrl());
        return route;
    }
}
