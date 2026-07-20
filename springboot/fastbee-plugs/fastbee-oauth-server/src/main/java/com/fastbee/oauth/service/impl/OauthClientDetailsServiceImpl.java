package com.fastbee.oauth.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import java.util.*;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.oauth.convert.OauthClientDetailsConvert;
import com.fastbee.oauth.domain.OauthClientDetails;
import com.fastbee.oauth.domain.vo.OauthClientDetailsVO;
import com.fastbee.oauth.mapper.OauthClientDetailsMapper;
import com.fastbee.oauth.service.IOauthClientDetailsService;

/**
 * 云云对接Service业务层处理
 *
 * @author kerwincui
 * @date 2022-02-07
 */
@Service
public class OauthClientDetailsServiceImpl extends ServiceImpl<OauthClientDetailsMapper,OauthClientDetails> implements IOauthClientDetailsService
{
    @Resource
    private OauthClientDetailsMapper oauthClientDetailsMapper;

    /**
     * 查询云云对接
     *
     * @param id 云云对接主键
     * @return 云云对接
     */
    @Override
    public OauthClientDetails selectOauthClientDetailsById(Long id)
    {
        return oauthClientDetailsMapper.selectById(id);
    }

    /**
     * 查询云云对接列表
     *
     * @param oauthClientDetails 云云对接
     * @return 云云对接
     */
    @Override
    public List<OauthClientDetailsVO> selectOauthClientDetailsList(OauthClientDetails oauthClientDetails)
    {
        // 查询所属机构
        SysUser user = getLoginUser().getUser();
        oauthClientDetails.setTenantId(user.getDept().getDeptUserId());
        LambdaQueryWrapper<OauthClientDetails> lqw = buildQueryWrapper(oauthClientDetails);
        List<OauthClientDetails> oauthClientDetailsList = baseMapper.selectList(lqw);
        return OauthClientDetailsConvert.INSTANCE.convertOauthClientDetailsVOList(oauthClientDetailsList);
    }

    /**
     * 查询【请填写功能名称】分页列表
     *
     * @param oauthClientDetails 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    @DataScope()
    public Page<OauthClientDetailsVO> pageOauthClientDetailsVO(OauthClientDetails oauthClientDetails) {
//        SysUser user = getLoginUser().getUser();
//        oauthClientDetails.setTenantId(user.getDept().getDeptUserId());
        LambdaQueryWrapper<OauthClientDetails> lqw = buildQueryWrapper(oauthClientDetails);
        Page<OauthClientDetails> oauthClientDetailsPage = baseMapper.selectPage(new Page<>(oauthClientDetails.getPageNum(), oauthClientDetails.getPageSize()), lqw);
        return OauthClientDetailsConvert.INSTANCE.convertOauthClientDetailsVOPage(oauthClientDetailsPage);
    }

    private LambdaQueryWrapper<OauthClientDetails> buildQueryWrapper(OauthClientDetails query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<OauthClientDetails> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(query.getClientId()), OauthClientDetails::getClientId, query.getClientId());
        lqw.eq(StringUtils.isNotBlank(query.getResourceIds()), OauthClientDetails::getResourceIds, query.getResourceIds());
        lqw.eq(StringUtils.isNotBlank(query.getClientSecret()), OauthClientDetails::getClientSecret, query.getClientSecret());
        lqw.eq(StringUtils.isNotBlank(query.getScope()), OauthClientDetails::getScope, query.getScope());
        lqw.eq(StringUtils.isNotBlank(query.getAuthorizedGrantTypes()), OauthClientDetails::getAuthorizedGrantTypes, query.getAuthorizedGrantTypes());
        lqw.eq(StringUtils.isNotBlank(query.getWebServerRedirectUri()), OauthClientDetails::getWebServerRedirectUri, query.getWebServerRedirectUri());
        lqw.eq(StringUtils.isNotBlank(query.getAuthorities()), OauthClientDetails::getAuthorities, query.getAuthorities());
        lqw.eq(query.getAccessTokenValidity() != null, OauthClientDetails::getAccessTokenValidity, query.getAccessTokenValidity());
        lqw.eq(query.getRefreshTokenValidity() != null, OauthClientDetails::getRefreshTokenValidity, query.getRefreshTokenValidity());
        lqw.eq(StringUtils.isNotBlank(query.getAdditionalInformation()), OauthClientDetails::getAdditionalInformation, query.getAdditionalInformation());
        lqw.eq(StringUtils.isNotBlank(query.getAutoapprove()), OauthClientDetails::getAutoapprove, query.getAutoapprove());
        lqw.eq(query.getType() != null, OauthClientDetails::getType, query.getType());
        lqw.eq(query.getStatus() != null, OauthClientDetails::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getIcon()), OauthClientDetails::getIcon, query.getIcon());
        lqw.eq(StringUtils.isNotBlank(query.getCloudSkillId()), OauthClientDetails::getCloudSkillId, query.getCloudSkillId());
        lqw.eq(query.getTenantId() != null, OauthClientDetails::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), OauthClientDetails::getTenantName, query.getTenantName());

        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }

        return lqw;
    }

    /**
     * 新增云云对接
     *
     * @param oauthClientDetails 云云对接
     * @return 结果
     */
    @Override
    public AjaxResult insertOauthClientDetails(OauthClientDetails oauthClientDetails)
    {
        SysUser user = getLoginUser().getUser();
        if (null == user.getDept() || null == user.getDept().getDeptUserId()) {
            throw new ServiceException(MessageUtils.message("only.allow.tenant.config"));
        }
        oauthClientDetails.setTenantId(user.getDept().getDeptUserId());
        oauthClientDetails.setTenantName(user.getDept().getDeptName());
        oauthClientDetails.setCreateBy(user.getUserName());
//        OauthClientDetails oauthClientDetails1 = oauthClientDetailsMapper.selectOauthClientDetailsByType(oauthClientDetails.getType(), oauthClientDetails.getTenantId());
//        if (oauthClientDetails1 != null) {
//            return AjaxResult.error(MessageUtils.message("add.fail.same.client.can.config.one"));
//        }
        LambdaQueryWrapper<OauthClientDetails> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OauthClientDetails::getClientId, oauthClientDetails.getClientId());
        OauthClientDetails oauthClientDetails2 = oauthClientDetailsMapper.selectOne(queryWrapper);
        if (oauthClientDetails2 != null) {
            return AjaxResult.error(StringUtils.format(MessageUtils.message("client.id.is.exist"), oauthClientDetails.getClientId()));
        }
        return oauthClientDetailsMapper.insert(oauthClientDetails) > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 修改云云对接
     *
     * @param oauthClientDetails 云云对接
     * @return 结果
     */
    @Override
    public AjaxResult updateOauthClientDetails(OauthClientDetails oauthClientDetails)
    {
        LambdaQueryWrapper<OauthClientDetails> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OauthClientDetails::getClientId, oauthClientDetails.getClientId());
        OauthClientDetails oauthClientDetails1 = oauthClientDetailsMapper.selectOne(queryWrapper);
        if (oauthClientDetails1 != null && !Objects.equals(oauthClientDetails1.getId(), oauthClientDetails.getId())) {
            return AjaxResult.error(StringUtils.format(MessageUtils.message("client.id.is.exist"), oauthClientDetails.getClientId()));
        }
        return oauthClientDetailsMapper.updateById(oauthClientDetails) > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 批量删除云云对接
     *
     * @param ids 需要删除的云云对接主键
     * @return 结果
     */
    @Override
    public int deleteOauthClientDetailsByIds(Long[] ids)
    {
        return oauthClientDetailsMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除云云对接信息
     *
     * @param clientId 云云对接主键
     * @return 结果
     */
    @Override
    public int deleteOauthClientDetailsByClientId(String clientId)
    {
        return oauthClientDetailsMapper.deleteById(clientId);
    }

    @Override
    public OauthClientDetails validOAuthClientFromCache(String clientId, String clientSecret, String grantType, Collection<String> scopes, String redirectUri) {
        // 校验客户端存在、且开启
        OauthClientDetails client = this.getOAuth2ClientFromCache(clientId);
        if (client == null) {
            throw new ServiceException(MessageUtils.message("oauthClientDetail.client.not.exist"));
        }
        if (client.getStatus() != null && 0 != client.getStatus()) {
            throw new ServiceException(MessageUtils.message("oauthClientDetail.client.disabled"));
        }

        // 校验客户端密钥
        if (StrUtil.isNotEmpty(clientSecret) && ObjectUtil.notEqual(client.getClientSecret(), clientSecret)) {
            throw new ServiceException(MessageUtils.message("oauthClientDetail.invalid.client.set"));
        }
        // 校验授权方式
        if (StrUtil.isNotEmpty(grantType) && !splitToSet(client.getAuthorizedGrantTypes()).contains(grantType)) {
            throw new ServiceException("不支持该授权类型");
        }
        // 校验授权范围
        if (scopes != null && !scopes.isEmpty() && !splitToSet(client.getScope()).containsAll(scopes)) {
            throw new ServiceException("授权范围过大");
        }
        // 校验回调地址
        Set<String> redirectUris = splitToSet(client.getWebServerRedirectUri());
        if (StrUtil.isNotEmpty(redirectUri) && !redirectUris.contains("*") && !redirectUris.contains(redirectUri)) {
            throw new ServiceException(StringUtils.format(MessageUtils.message("oauthClientDetail.invalid.redirects"), redirectUri));
        }
        return client;
    }

    private OauthClientDetails getOAuth2ClientFromCache(String clientId) {
        return oauthClientDetailsMapper.selectOauthClientDetailsByClientId(clientId);
    }

    private Set<String> splitToSet(String value) {
        Set<String> result = new HashSet<>();
        if (StrUtil.isEmpty(value)) {
            return result;
        }
        for (String item : value.split(",")) {
            if (StrUtil.isNotBlank(item)) {
                result.add(item.trim());
            }
        }
        return result;
    }
}
