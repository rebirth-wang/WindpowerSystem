package com.fastbee.social.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.utils.StringUtils;
import com.fastbee.social.convert.SocialPlatformConvert;
import com.fastbee.social.domain.SocialPlatform;
import com.fastbee.social.mapper.SocialPlatformMapper;
import com.fastbee.social.model.vo.SocialPlatformVO;
import com.fastbee.social.service.ISocialPlatformService;

/**
 * 第三方登录平台控制Service业务层处理
 *
 * @author kerwincui
 * @date 2022-04-11
 */
@Service
public class SocialPlatformServiceImpl extends ServiceImpl<SocialPlatformMapper,SocialPlatform> implements ISocialPlatformService
{
    @Resource
    private SocialPlatformMapper socialPlatformMapper;

    @Override
    public SocialPlatform selectSocialPlatformByPlatform(String platform) {
        LambdaQueryWrapper<SocialPlatform> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SocialPlatform::getPlatform, platform);
        return socialPlatformMapper.selectOne(wrapper);
    }

    @Override
    public Page<SocialPlatformVO> pageSocialPlatformVO(SocialPlatform socialPlatform) {
        LambdaQueryWrapper<SocialPlatform> lqw = buildQueryWrapper(socialPlatform);
        Page<SocialPlatform> socialPlatformPage = baseMapper.selectPage(new Page<>(socialPlatform.getPageNum(), socialPlatform.getPageSize()), lqw);
        return SocialPlatformConvert.INSTANCE.convertSocialPlatformPage(socialPlatformPage);
    }

    /**
     * 查询第三方登录平台控制列表
     *
     * @param socialPlatform 第三方登录平台控制
     * @return 第三方登录平台控制
     */
    @Override
    public List<SocialPlatformVO> selectSocialPlatformVOList(SocialPlatform socialPlatform) {
        LambdaQueryWrapper<SocialPlatform> lqw = buildQueryWrapper(socialPlatform);
        List<SocialPlatform> socialPlatformList = baseMapper.selectList(lqw);
        return SocialPlatformConvert.INSTANCE.convertSocialPlatformVOList(socialPlatformList);
    }

    /**
     * 查询第三方登录平台控制
     *
     * @param socialPlatformId 主键
     * @return 第三方登录平台控制
     */
    @Override
    @Cacheable(cacheNames = "SocialPlatform", key = "#socialPlatformId")
    // 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
    public SocialPlatform queryByIdWithCache(Long socialPlatformId){
        return this.getById(socialPlatformId);
    }

    /**
     * 新增第三方登录平台控制
     *
     * @param add 第三方登录平台控制
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(SocialPlatform add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改第三方登录平台控制
     *
     * @param update 第三方登录平台控制
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "SocialPlatform", key = "#update.socialPlatformId")
    public Boolean updateWithCache(SocialPlatform update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 校验并批量删除第三方登录平台控制信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "SocialPlatform", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }

    private LambdaQueryWrapper<SocialPlatform> buildQueryWrapper(SocialPlatform query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SocialPlatform> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(query.getPlatform()), SocialPlatform::getPlatform, query.getPlatform());
        lqw.eq(query.getStatus() != null, SocialPlatform::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getClientId()), SocialPlatform::getClientId, query.getClientId());
        lqw.eq(StringUtils.isNotBlank(query.getSecretKey()), SocialPlatform::getSecretKey, query.getSecretKey());
        lqw.eq(StringUtils.isNotBlank(query.getRedirectUri()), SocialPlatform::getRedirectUri, query.getRedirectUri());
        lqw.eq(StringUtils.isNotBlank(query.getBindUri()), SocialPlatform::getBindUri, query.getBindUri());
        lqw.eq(StringUtils.isNotBlank(query.getRedirectLoginUri()), SocialPlatform::getRedirectLoginUri, query.getRedirectLoginUri());
        lqw.eq(StringUtils.isNotBlank(query.getErrorMsgUri()), SocialPlatform::getErrorMsgUri, query.getErrorMsgUri());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SocialPlatform::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SocialPlatform entity){
        //TODO 做一些数据校验,如唯一约束
    }
}
