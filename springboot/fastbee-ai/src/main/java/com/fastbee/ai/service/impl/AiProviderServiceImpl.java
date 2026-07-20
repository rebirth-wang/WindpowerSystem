package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.ai.convert.AiProviderConvert;
import com.fastbee.ai.domain.AiModel;
import com.fastbee.ai.domain.AiProvider;
import com.fastbee.ai.mapper.AiModelMapper;
import com.fastbee.ai.mapper.AiProviderMapper;
import com.fastbee.ai.model.vo.AiProviderVO;
import com.fastbee.ai.service.AiRuntimeModelSnapshotService;
import com.fastbee.ai.service.IAiProviderService;
import com.fastbee.ai.support.AiSecuritySupport;
import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 厂商服务实现。
 */
@Service
public class AiProviderServiceImpl extends ServiceImpl<AiProviderMapper, AiProvider> implements IAiProviderService {

    @Resource
    private AiModelMapper aiModelMapper;

    @Resource
    private AiRuntimeModelSnapshotService aiRuntimeModelSnapshotService;

    @Override
    @DataScope
    public AiProvider selectAiProvider(AiProvider aiProvider) {
        return this.getOne(buildQueryWrapper(aiProvider));
    }

    @Override
    @DataScope
    public AiProviderVO selectAiProviderVO(AiProvider aiProvider) {
        return buildProviderVO(selectAiProvider(aiProvider));
    }

    @Override
    @DataScope
    public Page<AiProvider> pageAiProvider(AiProvider aiProvider) {
        LambdaQueryWrapper<AiProvider> lqw = buildQueryWrapper(aiProvider);
        lqw.orderByAsc(AiProvider::getSortNum).orderByDesc(AiProvider::getCreateTime);
        return baseMapper.selectPage(new Page<>(aiProvider.getPageNum(), aiProvider.getPageSize()), lqw);
    }

    @Override
    @DataScope
    public Page<AiProviderVO> pageAiProviderVO(AiProvider aiProvider) {
        Page<AiProvider> providerPage = pageAiProvider(aiProvider);
        Page<AiProviderVO> providerVOPage = new Page<>(providerPage.getCurrent(), providerPage.getSize(), providerPage.getTotal());
        providerVOPage.setRecords(buildProviderVOList(providerPage.getRecords()));
        return providerVOPage;
    }

    @Override
    public int insertAiProvider(AiProvider aiProvider) {
        validProviderBeforeSave(aiProvider);
        SysUser user = AiSecuritySupport.getCurrentUser();
        aiProvider.setTenantId(AiSecuritySupport.resolveTenantId(user));
        aiProvider.setTenantName(AiSecuritySupport.resolveTenantName(user));
        aiProvider.setCreateBy(AiSecuritySupport.resolveUsername());
        aiProvider.setCreateTime(AiSecuritySupport.now());
        aiProvider.setUpdateBy(AiSecuritySupport.resolveUsername());
        aiProvider.setUpdateTime(AiSecuritySupport.now());
        if (StringUtils.isBlank(aiProvider.getRegionProfile())) {
            aiProvider.setRegionProfile("CN");
        }
        if (StringUtils.isBlank(aiProvider.getAuthType())) {
            aiProvider.setAuthType("API_KEY");
        }
        if (aiProvider.getSortNum() == null) {
            aiProvider.setSortNum(0);
        }
        if (StringUtils.isBlank(aiProvider.getStatus())) {
            aiProvider.setStatus("0");
        }
        int rows = baseMapper.insert(aiProvider);
        if (rows > 0) {
            aiRuntimeModelSnapshotService.evictSnapshotsByProviderId(aiProvider.getProviderId());
        }
        return rows;
    }

    @Override
    public int updateAiProvider(AiProvider aiProvider) {
        validProviderBeforeSave(aiProvider);
        AiProvider old = baseMapper.selectById(aiProvider.getProviderId());
        if (old == null) {
            throw new ServiceException(message("ai.provider.update.target.not.found"));
        }
        if (StringUtils.isBlank(aiProvider.getApiKeyCipher())) {
            aiProvider.setApiKeyCipher(old.getApiKeyCipher());
        }
        aiProvider.setUpdateBy(AiSecuritySupport.resolveUsername());
        aiProvider.setUpdateTime(AiSecuritySupport.now());
        int rows = baseMapper.updateById(aiProvider);
        if (rows > 0) {
            aiRuntimeModelSnapshotService.evictSnapshotsByProviderId(aiProvider.getProviderId());
        }
        return rows;
    }

    @Override
    public int updateAiProviderStatus(AiProvider aiProvider) {
        AiProvider old = baseMapper.selectById(aiProvider.getProviderId());
        if (old == null) {
            throw new ServiceException(message("ai.provider.status.target.not.found"));
        }
        AiProvider update = new AiProvider();
        update.setProviderId(aiProvider.getProviderId());
        update.setStatus(aiProvider.getStatus());
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        int rows = baseMapper.updateById(update);
        if (rows > 0) {
            aiRuntimeModelSnapshotService.evictSnapshotsByProviderId(aiProvider.getProviderId());
        }
        return rows;
    }

    @Override
    public int deleteAiProviderByIds(Long[] providerIds) {
        Long modelCount = aiModelMapper.selectCount(Wrappers.<AiModel>lambdaQuery()
                .in(AiModel::getProviderId, Arrays.asList(providerIds)));
        if (modelCount != null && modelCount > 0) {
            throw new ServiceException(message("ai.provider.delete.model.exists"));
        }
        int rows = baseMapper.deleteBatchIds(Arrays.asList(providerIds));
        if (rows > 0) {
            for (Long providerId : providerIds) {
                aiRuntimeModelSnapshotService.evictSnapshotsByProviderId(providerId);
            }
        }
        return rows;
    }

    @Override
    public List<AiProvider> listEnabledProviders() {
        SysUser user = AiSecuritySupport.getCurrentUser();
        Long tenantId = AiSecuritySupport.resolveTenantId(user);
        LambdaQueryWrapper<AiProvider> lqw = Wrappers.lambdaQuery();
        lqw.eq(AiProvider::getStatus, "0");
        if (user != null && !user.isAdmin() && tenantId != null) {
            lqw.eq(AiProvider::getTenantId, tenantId);
        }
        lqw.orderByAsc(AiProvider::getSortNum).orderByAsc(AiProvider::getProviderId);
        return baseMapper.selectList(lqw);
    }

    @Override
    public List<AiProviderVO> listEnabledProviderVOs() {
        return buildProviderVOList(listEnabledProviders());
    }

    private LambdaQueryWrapper<AiProvider> buildQueryWrapper(AiProvider query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<AiProvider> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getProviderId() != null, AiProvider::getProviderId, query.getProviderId());
        lqw.eq(StringUtils.isNotBlank(query.getProviderCode()), AiProvider::getProviderCode, query.getProviderCode());
        lqw.like(StringUtils.isNotBlank(query.getProviderName()), AiProvider::getProviderName, query.getProviderName());
        lqw.eq(StringUtils.isNotBlank(query.getRegionProfile()), AiProvider::getRegionProfile, query.getRegionProfile());
        lqw.eq(StringUtils.isNotBlank(query.getAuthType()), AiProvider::getAuthType, query.getAuthType());
        lqw.eq(StringUtils.isNotBlank(query.getStatus()), AiProvider::getStatus, query.getStatus());
        lqw.eq(query.getTenantId() != null, AiProvider::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), AiProvider::getTenantName, query.getTenantName());

        if (Objects.nonNull(params.get("beginTime")) && Objects.nonNull(params.get("endTime"))) {
            lqw.between(AiProvider::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))) {
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        return lqw;
    }

    private void validProviderBeforeSave(AiProvider aiProvider) {
        if (StringUtils.isBlank(aiProvider.getProviderCode())) {
            throw new ServiceException(message("ai.provider.code.required"));
        }
        if (StringUtils.isBlank(aiProvider.getProviderName())) {
            throw new ServiceException(message("ai.provider.name.required"));
        }
        Long count = baseMapper.selectCount(Wrappers.<AiProvider>lambdaQuery()
                .eq(AiProvider::getProviderCode, aiProvider.getProviderCode())
                .ne(aiProvider.getProviderId() != null, AiProvider::getProviderId, aiProvider.getProviderId()));
        if (count != null && count > 0) {
            throw new ServiceException(message("ai.provider.code.exists"));
        }
    }

    private List<AiProviderVO> buildProviderVOList(List<AiProvider> providerList) {
        if (providerList == null || providerList.isEmpty()) {
            return new ArrayList<>();
        }
        List<AiProviderVO> providerVOList = new ArrayList<>(providerList.size());
        for (AiProvider provider : providerList) {
            providerVOList.add(buildProviderVO(provider));
        }
        return providerVOList;
    }

    private AiProviderVO buildProviderVO(AiProvider provider) {
        if (provider == null) {
            return null;
        }
        AiProviderVO providerVO = AiProviderConvert.INSTANCE.convertAiProviderVO(provider);
        String apiKeyCipher = provider.getApiKeyCipher();
        providerVO.setHasApiKey(StringUtils.isNotBlank(apiKeyCipher));
        providerVO.setApiKeyPreview(AiSecuritySupport.maskSecret(apiKeyCipher));
        return providerVO;
    }
}
