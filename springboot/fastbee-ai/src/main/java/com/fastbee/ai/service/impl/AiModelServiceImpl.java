package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.ai.convert.AiModelConvert;
import com.fastbee.ai.domain.AiModel;
import com.fastbee.ai.domain.AiProvider;
import com.fastbee.ai.mapper.AiModelMapper;
import com.fastbee.ai.mapper.AiProviderMapper;
import com.fastbee.ai.model.vo.AiModelVO;
import com.fastbee.ai.model.vo.AiProviderModelGroupVO;
import com.fastbee.ai.service.AiRuntimeModelSnapshotService;
import com.fastbee.ai.service.IAiModelService;
import com.fastbee.ai.support.AiSecuritySupport;
import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 模型服务实现。
 */
@Service
public class AiModelServiceImpl extends ServiceImpl<AiModelMapper, AiModel> implements IAiModelService {

    @Resource
    private AiProviderMapper aiProviderMapper;

    @Resource
    private AiRuntimeModelSnapshotService aiRuntimeModelSnapshotService;

    @Override
    @DataScope
    public AiModel selectAiModel(AiModel aiModel) {
        return this.getOne(buildQueryWrapper(aiModel));
    }

    @Override
    @DataScope
    public AiModelVO selectAiModelVO(AiModel aiModel) {
        AiModel result = selectAiModel(aiModel);
        if (result == null) {
            return null;
        }
        AiModelVO modelVO = AiModelConvert.INSTANCE.convertAiModelVO(result);
        enrichProviderVO(Collections.singletonList(modelVO));
        return modelVO;
    }

    @Override
    @DataScope
    public Page<AiModel> pageAiModel(AiModel aiModel) {
        LambdaQueryWrapper<AiModel> lqw = buildQueryWrapper(aiModel);
        lqw.orderByDesc(AiModel::getIsDefault).orderByDesc(AiModel::getCreateTime);
        return baseMapper.selectPage(new Page<>(aiModel.getPageNum(), aiModel.getPageSize()), lqw);
    }

    @Override
    @DataScope
    public Page<AiModelVO> pageAiModelVO(AiModel aiModel) {
        Page<AiModel> modelPage = pageAiModel(aiModel);
        Page<AiModelVO> modelVOPage = AiModelConvert.INSTANCE.convertAiModelVOPage(modelPage);
        if (modelVOPage != null && modelVOPage.getRecords() != null) {
            enrichProviderVO(modelVOPage.getRecords());
        }
        return modelVOPage;
    }

    @Override
    public int insertAiModel(AiModel aiModel) {
        validModelBeforeSave(aiModel);
        SysUser user = AiSecuritySupport.getCurrentUser();
        aiModel.setTenantId(AiSecuritySupport.resolveTenantId(user));
        aiModel.setTenantName(AiSecuritySupport.resolveTenantName(user));
        aiModel.setCreateBy(AiSecuritySupport.resolveUsername());
        aiModel.setCreateTime(AiSecuritySupport.now());
        aiModel.setUpdateBy(AiSecuritySupport.resolveUsername());
        aiModel.setUpdateTime(AiSecuritySupport.now());
        if (StringUtils.isBlank(aiModel.getModelType())) {
            aiModel.setModelType("CHAT");
        }
        if (StringUtils.isBlank(aiModel.getIsDefault())) {
            aiModel.setIsDefault("0");
        }
        if (StringUtils.isBlank(aiModel.getStatus())) {
            aiModel.setStatus("0");
        }
        int rows = baseMapper.insert(aiModel);
        refreshDefaultFlag(aiModel);
        if (rows > 0) {
            aiRuntimeModelSnapshotService.evictSnapshotsByModelId(aiModel.getModelId());
        }
        return rows;
    }

    @Override
    public int updateAiModel(AiModel aiModel) {
        validModelBeforeSave(aiModel);
        AiModel old = baseMapper.selectById(aiModel.getModelId());
        if (old == null) {
            throw new ServiceException(message("ai.model.update.target.not.found"));
        }
        aiModel.setUpdateBy(AiSecuritySupport.resolveUsername());
        aiModel.setUpdateTime(AiSecuritySupport.now());
        int rows = baseMapper.updateById(aiModel);
        refreshDefaultFlag(aiModel);
        if (rows > 0) {
            aiRuntimeModelSnapshotService.evictSnapshotsByModelId(aiModel.getModelId());
        }
        return rows;
    }

    @Override
    public int updateAiModelStatus(AiModel aiModel) {
        AiModel old = baseMapper.selectById(aiModel.getModelId());
        if (old == null) {
            throw new ServiceException(message("ai.model.status.target.not.found"));
        }
        AiModel update = new AiModel();
        update.setModelId(aiModel.getModelId());
        update.setStatus(aiModel.getStatus());
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        int rows = baseMapper.updateById(update);
        if (rows > 0) {
            aiRuntimeModelSnapshotService.evictSnapshotsByModelId(aiModel.getModelId());
        }
        return rows;
    }

    @Override
    public int deleteAiModelByIds(Long[] modelIds) {
        int rows = baseMapper.deleteBatchIds(Arrays.asList(modelIds));
        if (rows > 0) {
            for (Long modelId : modelIds) {
                aiRuntimeModelSnapshotService.evictSnapshotsByModelId(modelId);
            }
        }
        return rows;
    }

    @Override
    public AiModel selectByModelCode(String modelCode) {
        if (StringUtils.isBlank(modelCode)) {
            return null;
        }
        SysUser user = AiSecuritySupport.getCurrentUser();
        Long tenantId = AiSecuritySupport.resolveTenantId(user);
        LambdaQueryWrapper<AiModel> lqw = Wrappers.lambdaQuery();
        lqw.eq(AiModel::getModelCode, modelCode);
        lqw.eq(AiModel::getStatus, "0");
        if (user != null && !user.isAdmin() && tenantId != null) {
            lqw.eq(AiModel::getTenantId, tenantId);
        }
        return baseMapper.selectOne(lqw);
    }

    @Override
    public List<AiProviderModelGroupVO> listGroupedOptions() {
        SysUser user = AiSecuritySupport.getCurrentUser();
        Long tenantId = AiSecuritySupport.resolveTenantId(user);
        LambdaQueryWrapper<AiProvider> providerQuery = Wrappers.lambdaQuery();
        providerQuery.eq(AiProvider::getStatus, "0");
        if (user != null && !user.isAdmin() && tenantId != null) {
            providerQuery.eq(AiProvider::getTenantId, tenantId);
        }
        providerQuery.orderByAsc(AiProvider::getSortNum).orderByAsc(AiProvider::getProviderId);
        List<AiProvider> providerList = aiProviderMapper.selectList(providerQuery);
        if (providerList.isEmpty()) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<AiModel> modelQuery = Wrappers.lambdaQuery();
        modelQuery.eq(AiModel::getStatus, "0");
        modelQuery.in(AiModel::getProviderId, providerList.stream().map(AiProvider::getProviderId).collect(Collectors.toList()));
        modelQuery.orderByDesc(AiModel::getIsDefault).orderByAsc(AiModel::getModelName);
        List<AiModel> modelList = baseMapper.selectList(modelQuery);
        Map<Long, List<AiModel>> modelGroup = modelList.stream()
                .collect(Collectors.groupingBy(AiModel::getProviderId, LinkedHashMap::new, Collectors.toList()));

        List<AiProviderModelGroupVO> result = new ArrayList<>();
        for (AiProvider provider : providerList) {
            AiProviderModelGroupVO groupVO = new AiProviderModelGroupVO();
            groupVO.setProviderId(provider.getProviderId());
            groupVO.setProviderCode(provider.getProviderCode());
            groupVO.setProviderName(provider.getProviderName());
            groupVO.setRegionProfile(provider.getRegionProfile());
            List<AiProviderModelGroupVO.ModelItem> modelItems = new ArrayList<>();
            for (AiModel model : modelGroup.getOrDefault(provider.getProviderId(), new ArrayList<>())) {
                AiProviderModelGroupVO.ModelItem item = new AiProviderModelGroupVO.ModelItem();
                item.setModelId(model.getModelId());
                item.setModelCode(model.getModelCode());
                item.setModelName(model.getModelName());
                item.setModelType(model.getModelType());
                item.setIsDefault(model.getIsDefault());
                modelItems.add(item);
            }
            groupVO.setModels(modelItems);
            result.add(groupVO);
        }
        return result;
    }

    private LambdaQueryWrapper<AiModel> buildQueryWrapper(AiModel query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<AiModel> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getModelId() != null, AiModel::getModelId, query.getModelId());
        lqw.eq(query.getProviderId() != null, AiModel::getProviderId, query.getProviderId());
        lqw.like(StringUtils.isNotBlank(query.getModelCode()), AiModel::getModelCode, query.getModelCode());
        lqw.like(StringUtils.isNotBlank(query.getModelName()), AiModel::getModelName, query.getModelName());
        lqw.eq(StringUtils.isNotBlank(query.getModelType()), AiModel::getModelType, query.getModelType());
        lqw.eq(StringUtils.isNotBlank(query.getIsDefault()), AiModel::getIsDefault, query.getIsDefault());
        lqw.eq(StringUtils.isNotBlank(query.getStatus()), AiModel::getStatus, query.getStatus());
        lqw.eq(query.getTenantId() != null, AiModel::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), AiModel::getTenantName, query.getTenantName());

        if (Objects.nonNull(params.get("beginTime")) && Objects.nonNull(params.get("endTime"))) {
            lqw.between(AiModel::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))) {
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        return lqw;
    }

    private void validModelBeforeSave(AiModel aiModel) {
        if (aiModel.getProviderId() == null) {
            throw new ServiceException(message("ai.model.provider.required"));
        }
        AiProvider provider = aiProviderMapper.selectById(aiModel.getProviderId());
        if (provider == null) {
            throw new ServiceException(message("ai.model.provider.not.exists.refresh"));
        }
        if (StringUtils.isBlank(aiModel.getModelCode())) {
            throw new ServiceException(message("ai.model.code.required"));
        }
        if (StringUtils.isBlank(aiModel.getModelName())) {
            throw new ServiceException(message("ai.model.name.required"));
        }
        Long count = baseMapper.selectCount(Wrappers.<AiModel>lambdaQuery()
                .eq(AiModel::getModelCode, aiModel.getModelCode())
                .ne(aiModel.getModelId() != null, AiModel::getModelId, aiModel.getModelId()));
        if (count != null && count > 0) {
            throw new ServiceException(message("ai.model.code.exists"));
        }
    }

    private void refreshDefaultFlag(AiModel aiModel) {
        if (!"1".equals(aiModel.getIsDefault()) || aiModel.getModelId() == null) {
            return;
        }
        this.update(Wrappers.<AiModel>lambdaUpdate()
                .set(AiModel::getIsDefault, "0")
                .eq(AiModel::getProviderId, aiModel.getProviderId())
                .eq(aiModel.getTenantId() != null, AiModel::getTenantId, aiModel.getTenantId())
                .ne(AiModel::getModelId, aiModel.getModelId()));
    }

    private void enrichProviderVO(List<AiModelVO> modelVOList) {
        if (modelVOList == null || modelVOList.isEmpty()) {
            return;
        }
        Set<Long> providerIds = modelVOList.stream()
                .filter(Objects::nonNull)
                .map(AiModelVO::getProviderId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (providerIds.isEmpty()) {
            return;
        }
        Map<Long, AiProvider> providerMap = aiProviderMapper.selectBatchIds(providerIds).stream()
                .collect(Collectors.toMap(AiProvider::getProviderId, item -> item));
        for (AiModelVO modelVO : modelVOList) {
            if (modelVO == null) {
                continue;
            }
            AiProvider provider = providerMap.get(modelVO.getProviderId());
            if (provider != null) {
                modelVO.setProviderCode(provider.getProviderCode());
                modelVO.setProviderName(provider.getProviderName());
            }
        }
    }
}
